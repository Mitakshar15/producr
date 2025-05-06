package org.producr.api.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.producr.api.data.domain.pack.SamplePack;
import org.producr.api.data.domain.track.AudioSample;
import org.producr.api.data.domain.track.Beat;
import org.producr.api.data.domain.track.Track;
import org.producr.api.data.domain.track.TrackMetadata;
import org.producr.api.data.domain.user.User;
import org.producr.api.dtos.TrackUploadRequest;
import org.producr.api.mapper.TrackMgmtMapper;
import org.producr.api.service.impl.AudioUploadServiceImpl;
import org.producr.api.utils.enums.SampleType;
import org.producr.api.utils.enums.TrackType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProducrUtils {
  private final StorageUtils storageUtils;
  private final TrackMgmtMapper mapper;

  public void processZipFile(SamplePack samplePack, MultipartFile zipFile) throws IOException {
    // First scan to count files and select preview candidates
    List<String> selectedPreviewFiles = new ArrayList<>();
    long totalSize = 0;
    int totalSampleCount = 0;

    try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
      ZipEntry entry;
      List<ZipEntryInfo> audioEntries = new ArrayList<>();

      // First pass: collect information about all audio files
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory() && isSupportedAudioFile(entry.getName())) {
          audioEntries.add(new ZipEntryInfo(entry.getName(), entry.getSize()));
          totalSize += entry.getSize();
          totalSampleCount++;
        }
        zis.closeEntry();
      }

      // If we're only generating previews, select a subset
      // Sort by file types to get a diverse selection
      audioEntries.sort(
          (a, b) -> categorizeFileForSorting(a.filename) - categorizeFileForSorting(b.filename));

      // Select files across different categories (e.g., 5 samples or 20% of total)
      int previewCount = Math.min(5, (int) Math.ceil(audioEntries.size() * 0.2));
      for (int i = 0; i < Math.min(previewCount, audioEntries.size()); i++) {
        selectedPreviewFiles.add(audioEntries.get(i).filename);
      }

    }

    samplePack.setTotalSizeBytes(totalSize);
    samplePack.setSampleCount(totalSampleCount);

    // Second pass: actually extract and process the selected files
    try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
      ZipEntry entry;
      int processedSamples = 0;

      while ((entry = zis.getNextEntry()) != null) {
        String filename = entry.getName();

        if (!entry.isDirectory() && isSupportedAudioFile(filename)) {
          boolean shouldProcess = selectedPreviewFiles.contains(filename);

          if (shouldProcess) {
            // Extract and store the sample file
            String audioFileUrl = storageUtils.extractAndStoreZipEntry(zis, entry,
                "sample-packs/" + samplePack.getTitle() + "/samples");

            // Create AudioSample entity
            AudioSample sample = new AudioSample();
            sample.setSamplePack(samplePack);
            sample.setTitle(filename);
            sample.setAudioFileUrl(audioFileUrl);
            sample.setFileSizeBytes(entry.getSize());
            sample.setTrackLengthSeconds(3); // Default value
            sample.setProducer(samplePack.getProducer());

            // Extract audio metadata
            extractSampleMetadata(sample, audioFileUrl);

            // For preview mode, mark samples as previews and generate shortened versions
            String previewUrl = storageUtils.generatePreviewAudio(audioFileUrl);
            sample.setAudioFileUrl(previewUrl);
            sample.setIsPreviewSample(true);

            samplePack.addSample(sample);
            processedSamples++;
          }
        }
        zis.closeEntry();
      }

      samplePack.setPreviewSampleCount(processedSamples);
    }
  }

  public static class ZipEntryInfo {
    String filename;
    long size;

    ZipEntryInfo(String filename, long size) {
      this.filename = filename;
      this.size = size;
    }
  }

  // Helper method to categorize files for sorting
  public int categorizeFileForSorting(String filename) {
    filename = filename.toLowerCase();
    if (filename.contains("loop"))
      return 0;
    if (filename.contains("drum") || filename.contains("kick") || filename.contains("snare"))
      return 1;
    if (filename.contains("fx") || filename.contains("effect"))
      return 2;
    if (filename.contains("vocal") || filename.contains("vox"))
      return 3;
    return 4; // other
  }

  public boolean isSupportedAudioFile(String filename) {
    String lowerFilename = filename.toLowerCase();
    return lowerFilename.endsWith(".wav") || lowerFilename.endsWith(".mp3")
        || lowerFilename.endsWith(".aif") || lowerFilename.endsWith(".aiff");
  }

  public void extractSampleMetadata(AudioSample sample, String audioFileUrl) {
    try {
      Map<String, Object> metadata = storageUtils.extractAudioMetadata(audioFileUrl);
      sample.setLoopTempo(Integer.valueOf((String) metadata.getOrDefault("bpm", 0)));
      sample.setKey((String) metadata.getOrDefault("key", "Am"));
      sample.setTrackLengthSeconds((Integer) metadata.getOrDefault("durationSeconds", 0));
      sample.setSampleType(detectSampleType(sample.getTitle(), sample));
      TrackUploadRequest request = new TrackUploadRequest();
      request.setSampleType(mapper.toSampleTypeEnum(sample.getSampleType()));
      request.setTrackType(TrackUploadRequest.TrackTypeEnum.SAMPLE);
      sample.setMetadata(createTrackMetadata(sample, request, metadata));
      sample.setAudioFileUrl(audioFileUrl);
      sample.setWaveformData(storageUtils.generateWaveformData(audioFileUrl));
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  public SampleType detectSampleType(String filename, Track sample) {
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


  public Beat createBeat(TrackUploadRequest requestDto, User producer, String audioFileUrl,
      Integer trackLengthSeconds, Map<String, Object> metadata) {
    // Create base track
    Track baseTrack = new Track();
    baseTrack.setTitle(requestDto.getTitle());
    baseTrack.setDescription(requestDto.getDescription());
    baseTrack.setProducer(producer);
    baseTrack.setAudioFileUrl(audioFileUrl);
    baseTrack.setTrackLengthSeconds(trackLengthSeconds);
    baseTrack.setPublic(requestDto.getIsPublic());
    baseTrack.setDownloadable(requestDto.getIsDownloadable());
    baseTrack.setTrackType(TrackType.BEAT);
    // Create beat from base track
    Beat beat = new Beat(baseTrack);

    // Set beat-specific properties
    beat.setBpm(Integer.valueOf((String) metadata.getOrDefault("bpm", requestDto.getBpm())));
    beat.setKey(requestDto.getKey());
    beat.setTimeSignature(
        requestDto.getTimeSignature() != null ? requestDto.getTimeSignature() : "4/4");

    if (requestDto.getGenres() != null) {
      beat.setGenres(requestDto.getGenres());
    }

    if (requestDto.getMoods() != null) {
      beat.setMoods(requestDto.getMoods());
    }

    // Create and link track metadata
    TrackMetadata trackMetadata = createTrackMetadata(beat, requestDto, metadata);
    beat.setMetadata(trackMetadata);

    return beat;
  }

  public AudioSample createAudioSample(TrackUploadRequest requestDto, User producer,
      String audioFileUrl, Integer trackLengthSeconds, Map<String, Object> metadata) {
    // Create base track
    Track baseTrack = new Track();
    baseTrack.setTitle(requestDto.getTitle());
    baseTrack.setDescription(requestDto.getDescription());
    baseTrack.setProducer(producer);
    baseTrack.setAudioFileUrl(audioFileUrl);
    baseTrack.setTrackLengthSeconds(trackLengthSeconds);
    baseTrack.setPublic(requestDto.getIsPublic());
    baseTrack.setDownloadable(requestDto.getIsDownloadable());
    baseTrack.setTrackType(TrackType.SAMPLE);
    // Create audio sample from base track
    AudioSample sample = new AudioSample(baseTrack);
    SampleType sampleType = null;

    // Set sample-specific properties
    sample.setSampleType(
        requestDto.getSampleType() != null ? mapper.toSampleType(requestDto.getSampleType())
            : SampleType.ONE_SHOT);
    sample.setRootNote(requestDto.getRootNote());
    sample.setLoop(requestDto.getIsLoop());

    if (requestDto.getIsLoop()) {
      sample.setLoopTempo(requestDto.getLoopTempo());
    }

    sample.setAudioCategory(mapper.toAudioCategoryEnum(requestDto.getAudioCategory()));

    // Create and link track metadata
    TrackMetadata trackMetadata = createTrackMetadata(sample, requestDto, metadata);
    sample.setMetadata(trackMetadata);

    return sample;
  }

  public TrackMetadata createTrackMetadata(Track track, TrackUploadRequest requestDto,
      Map<String, Object> metadata) {
    TrackMetadata trackMetadata = new TrackMetadata();
    trackMetadata.setTrack(track);

    // Set musical properties
    trackMetadata
        .setBpm((Integer.valueOf((String) metadata.getOrDefault("bpm", requestDto.getBpm()))));
    trackMetadata.setKey(requestDto.getKey());

    // Set genre from beat if available
    if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.BEAT.toString())
        && requestDto.getGenres() != null && !requestDto.getGenres().isEmpty()) {
      trackMetadata.setGenre(requestDto.getGenres().iterator().next()); // Use first genre
    }

    // Set mood from beat if available
    if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.BEAT.toString())
        && requestDto.getMoods() != null && !requestDto.getMoods().isEmpty()) {
      trackMetadata.setMood(requestDto.getMoods().iterator().next()); // Use first mood
    }

    // Set loop information for samples
    if (requestDto.getTrackType().getValue().equalsIgnoreCase(TrackType.SAMPLE.toString())) {
      trackMetadata.setLoop(requestDto.getIsLoop());
    }

    // Set technical metadata from file analysis
    trackMetadata.setSampleRate((Integer) metadata.getOrDefault("sampleRate", 44100));
    trackMetadata.setBitDepth((Integer) metadata.getOrDefault("bitDepth", 16));
    trackMetadata.setFileFormat((String) metadata.getOrDefault("fileFormat", "MP3"));
    trackMetadata.setFileSizeBytes((Long) metadata.getOrDefault("fileSizeBytes", 0L));

    return trackMetadata;
  }
}
