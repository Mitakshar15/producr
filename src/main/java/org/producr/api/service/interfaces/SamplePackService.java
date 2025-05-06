package org.producr.api.service.interfaces;

import org.producr.api.data.domain.pack.SamplePack;
import org.producr.api.data.domain.track.AudioSample;
import org.producr.api.data.domain.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface SamplePackService {

  SamplePack createSamplePack(MultipartFile zipFile, String title, String description, Double price,
      User producer, boolean enablePreviews) throws IOException;


  Set<AudioSample> getPackPreviews(String packId);

  void publishPack(String packId);

  List<SamplePack> getPublishedPacks(int page, int size, User user);
}
