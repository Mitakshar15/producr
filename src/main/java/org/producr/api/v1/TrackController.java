package org.producr.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.producr.api.TrackControllerV1Api;
import org.producr.api.builder.ApiResponseBuilder;
import org.producr.api.config.security.jwt.JwtTokenUtil;
import org.producr.api.data.domain.user.User;
import org.producr.api.dtos.TrackFeedPageResponse;
import org.producr.api.dtos.TrackResponse;
import org.producr.api.dtos.TrackUploadRequest;
import org.producr.api.dtos.TrackUploadResponse;
import org.producr.api.mapper.TrackMgmtMapper;
import org.producr.api.service.interfaces.AudioTrackService;
import org.producr.api.service.interfaces.AudioUploadService;
import org.producr.api.service.interfaces.UserService;
import org.producr.api.utils.constants.Constants;
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
  private final AudioTrackService audioTrackService;
  private final UserService userService;


  @Override
  public ResponseEntity<TrackUploadResponse> uploadAudioFile(String authorization,
      TrackUploadRequest trackUploadRequest) throws Exception {
    String userName = jwtTokenUtil.getUsernameFromToken(authorization);
    TrackUploadResponse trackUploadResponse =
        mapper.toTrackUploadResponse(builder.buildSuccessApiResponse("TRACK UPLOAD SUCCESS"));
    trackUploadResponse.data(audioUploadService.uploadTrack(userName, trackUploadRequest));
    return new ResponseEntity<>(trackUploadResponse, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<TrackFeedPageResponse> getFeedTracks(String authorization, Integer page)
      throws Exception {
    TrackFeedPageResponse response = mapper.toTrackFeedPageResponse(
        builder.buildSuccessApiResponse(Constants.GET_TRACK_FEED_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    response.data(audioTrackService.getFeedTracks(user, page));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<TrackResponse> getUserTracks(String authorization) throws Exception {
    User user = userService.handleGetUserProfile(authorization);
    TrackResponse response = mapper.toTrackResponse(
        builder.buildSuccessApiResponse(Constants.GET_USER_TRACKS_SUCCESS_MESSAGE));
    response.data(mapper.toTrackFeedItemDtoList(audioTrackService.getUserTracks(user)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
