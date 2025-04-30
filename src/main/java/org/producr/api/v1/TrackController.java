package org.producr.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.producr.api.TrackControllerV1Api;
import org.producr.api.builder.ApiResponseBuilder;
import org.producr.api.config.security.jwt.JwtTokenUtil;
import org.producr.api.dtos.TrackUploadRequest;
import org.producr.api.dtos.TrackUploadResponse;
import org.producr.api.mapper.TrackMgmtMapper;
import org.producr.api.service.interfaces.AudioUploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TrackController implements TrackControllerV1Api {

  private final JwtTokenUtil jwtTokenUtil;
  private final TrackMgmtMapper mapper;
  private final ApiResponseBuilder builder;
  private final AudioUploadService audioUploadService;


  @Override
  public ResponseEntity<TrackUploadResponse> uploadAudioFile(String authorization,
      TrackUploadRequest trackUploadRequest) throws Exception {
    String userName = jwtTokenUtil.getUsernameFromToken(authorization);
    TrackUploadResponse trackUploadResponse =
        mapper.toTrackUploadResponse(builder.buildSuccessApiResponse("TRACK UPLOAD SUCCESS"));
    trackUploadResponse.data(audioUploadService.uploadTrack(userName, trackUploadRequest));
    return new ResponseEntity<>(trackUploadResponse, HttpStatus.OK);
  }
}
