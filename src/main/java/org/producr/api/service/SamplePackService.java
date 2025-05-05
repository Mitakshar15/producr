package org.producr.api.service;

import lombok.RequiredArgsConstructor;
import org.producr.api.data.domain.pack.SamplePack;
import org.producr.api.data.domain.track.AudioSample;
import org.producr.api.data.domain.track.Track;
import org.producr.api.data.domain.user.User;
import org.producr.api.data.repository.SamplePackRepository;
import org.producr.api.data.repository.UserRepository;
import org.producr.api.service.interfaces.UserService;
import org.producr.api.utils.enums.SampleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class SamplePackService {
  private final SamplePackRepository samplePackRepository;
  private final StorageService storageService;
  private final UserService userService;
  private final UserRepository userRepository;

  @Transactional(rollbackFor = Exception.class)
  public SamplePack createSamplePack(MultipartFile zipFile, String title, String description,
      Double price, String producerId, boolean enablePreviews) throws IOException {
    User producer = userRepository.findById(producerId)
        .orElseThrow(() -> new RuntimeException("Producer not found"));

    // Create and save the sample pack
    SamplePack samplePack = new SamplePack();
    samplePack.setTitle(title);
    samplePack.setDescription(description);
    samplePack.setPrice(price);
    samplePack.setProducer(producer);
    samplePack.setPreviewEnabled(enablePreviews);

    // Store the zip file
    String zipFileUrl = storageService.storeFile(zipFile, "sample-packs", producerId);
    samplePack.setZipFileUrl(zipFileUrl);

    // Process the zip file and extract samples
    processZipFile(samplePack, zipFile, enablePreviews);

    SamplePack pack = samplePackRepository.save(samplePack);
    return pack;
  }

  private void processZipFile(SamplePack samplePack, MultipartFile zipFile, boolean enablePreviews)
      throws IOException {
    try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
      ZipEntry entry;
      long totalSize = 0;
      int sampleCount = 0;

      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory() && isSupportedAudioFile(entry.getName())) {
          // Extract and store the sample file
          String audioFileUrl = storageService.extractAndStoreZipEntry(zis, entry,
              "sample-packs/" + samplePack.getTitle() + "/samples");

          // Create PackSample entity
          AudioSample sample = new AudioSample();
          sample.setSamplePack(samplePack);
          sample.setTitle(entry.getName());
          sample.setAudioFileUrl(audioFileUrl);
          sample.setFileSizeBytes(entry.getSize());
          sample.setTrackLengthSeconds(3);
          sample.setProducer(samplePack.getProducer());
          // Extract audio metadata
          extractAudioMetadata(sample, audioFileUrl);

          // Generate preview if enabled
          if (enablePreviews && shouldCreatePreview(sample)) {
            String previewUrl = storageService.generatePreviewAudio(audioFileUrl);
            sample.setAudioFileUrl(previewUrl);
            sample.setIsPreviewSample(true);
          }

          samplePack.addSample(sample);
          totalSize += entry.getSize();
          sampleCount++;
        }
        zis.closeEntry();
      }

      samplePack.setTotalSizeBytes(totalSize);
      samplePack.setSampleCount(sampleCount);
    }
  }

  private boolean isSupportedAudioFile(String filename) {
    String lowerFilename = filename.toLowerCase();
    return lowerFilename.endsWith(".wav") || lowerFilename.endsWith(".mp3")
        || lowerFilename.endsWith(".aif") || lowerFilename.endsWith(".aiff");
  }

  private void extractAudioMetadata(AudioSample sample, String audioFileUrl) {
    try {
      Map<String, Object> metadata = storageService.extractAudioMetadata(audioFileUrl);
      sample.setLoopTempo(Integer.valueOf((String) metadata.getOrDefault("bpm", 0)));
      sample.setKey((String) metadata.getOrDefault("key", "Am"));
      sample.setTrackLengthSeconds((Integer) metadata.getOrDefault("durationSeconds", 0));
      sample.setSampleType(detectSampleType(sample.getTitle(), sample));
    } catch (Exception e) {
      // Log error but continue processing
    }
  }

  private SampleType detectSampleType(String filename, Track sample) {
    filename = filename.toLowerCase();
    if (sample.getTrackLengthSeconds() > 8) {
      return SampleType.LOOP;
    } else if (filename.contains("drum") || filename.contains("kick") || filename.contains("snare")
        || filename.contains("hat")) {
      return SampleType.DRUM_HIT;
    } else if (filename.contains("fx") || filename.contains("effect")) {
      return SampleType.FX;
    } else {
      return SampleType.ONE_SHOT;
    }
  }

  private boolean shouldCreatePreview(AudioSample sample) {
    // Create previews for longer samples and loops
    return sample.getTrackLengthSeconds() > 4 || sample.getSampleType() == SampleType.LOOP;
  }

  @Transactional(readOnly = true)
  public Set<AudioSample> getPackPreviews(String packId) {
    SamplePack pack = samplePackRepository.findById(packId)
        .orElseThrow(() -> new RuntimeException("Sample pack not found"));

    return pack.getSamples().stream().filter(AudioSample::getIsPreviewSample)
        .collect(java.util.stream.Collectors.toSet());
  }

  @Transactional
  public void publishPack(String packId) {
    SamplePack pack = samplePackRepository.findById(packId)
        .orElseThrow(() -> new RuntimeException("Sample pack not found"));
    pack.setPublished(true);
    samplePackRepository.save(pack);
  }

  @Transactional(readOnly = true)
  public List<SamplePack> getPublishedPacks(int page, int size) {
    return samplePackRepository
        .findByPublished(org.springframework.data.domain.PageRequest.of(page, size));
  }
}
