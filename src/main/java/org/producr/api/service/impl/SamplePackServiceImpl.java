package org.producr.api.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.producr.api.data.domain.pack.SamplePack;
import org.producr.api.data.domain.track.AudioSample;
import org.producr.api.data.domain.user.User;
import org.producr.api.data.repository.SamplePackRepository;
import org.producr.api.data.repository.UserRepository;
import org.producr.api.mapper.TrackMgmtMapper;
import org.producr.api.utils.ProducrUtils;
import org.producr.api.utils.StorageUtils;
import org.producr.api.service.interfaces.SamplePackService;
import org.producr.api.service.interfaces.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class SamplePackServiceImpl implements SamplePackService {
  private final SamplePackRepository samplePackRepository;
  private final StorageUtils storageUtils;
  private final UserService userService;
  private final UserRepository userRepository;
  private final TrackMgmtMapper mapper;
  private final AudioUploadServiceImpl audioUploadServiceImpl;
  private final ProducrUtils producrUtils;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SamplePack createSamplePack(MultipartFile zipFile, String title, String description,
      Double price, User producer, boolean enablePreviews) throws IOException {
    // Create and save the sample pack
    SamplePack samplePack = new SamplePack();
    samplePack.setTitle(title);
    samplePack.setDescription(description);
    samplePack.setPrice(price);
    samplePack.setProducer(producer);
    samplePack.setPreviewEnabled(enablePreviews);

    // Store the zip file
    String zipFileUrl = storageUtils.storeFile(zipFile, "sample-packs", producer.getUsername());
    samplePack.setZipFileUrl(zipFileUrl);

    // Process the zip file and extract samples
    if (enablePreviews) {
      producrUtils.processZipFile(samplePack, zipFile);
    }
    return samplePackRepository.save(samplePack);
  }



  @Override
  @Transactional(readOnly = true)
  public Set<AudioSample> getPackPreviews(String packId) {
    SamplePack pack = samplePackRepository.findById(packId)
        .orElseThrow(() -> new RuntimeException("Sample pack not found"));

    return pack.getSamples().stream().filter(AudioSample::getIsPreviewSample)
        .collect(java.util.stream.Collectors.toSet());
  }

  @Override
  @Transactional
  public void publishPack(String packId) {
    SamplePack pack = samplePackRepository.findById(packId)
        .orElseThrow(() -> new RuntimeException("Sample pack not found"));
    pack.setPublished(true);
    samplePackRepository.save(pack);
  }

  @Override
  @Transactional(readOnly = true)
  public List<SamplePack> getPublishedPacks(int page, int size, User user) {
    return samplePackRepository
        .findByPublished(org.springframework.data.domain.PageRequest.of(page, size));
  }
}
