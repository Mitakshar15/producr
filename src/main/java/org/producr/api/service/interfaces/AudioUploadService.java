package org.producr.api.service.interfaces;

import org.producr.api.dtos.TrackUploadRequest;
import org.producr.api.dtos.TrackUploadResponseDto;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public interface AudioUploadService {
  TrackUploadResponseDto uploadTrack(String userName, TrackUploadRequest requestDto)
          throws IOException, UnsupportedAudioFileException;
}
