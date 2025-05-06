package org.producr.api.v1;

import lombok.RequiredArgsConstructor;
import org.producr.api.SamplePacksApi;
import org.producr.api.builder.ApiResponseBuilder;
import org.producr.api.data.domain.user.User;
import org.producr.api.dtos.AudioSampleResponse;
import org.producr.api.dtos.PublishedSamplePackResponse;
import org.producr.api.dtos.SamplePackResponse;
import org.producr.api.dtos.TrackBaseApiResponse;
import org.producr.api.mapper.TrackMgmtMapper;
import org.producr.api.service.impl.SamplePackServiceImpl;
import org.producr.api.service.interfaces.UserService;
import org.producr.api.utils.constants.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequiredArgsConstructor
@Validated
public class SamplePackController implements SamplePacksApi {
  private final SamplePackServiceImpl samplePackService;
  private final TrackMgmtMapper mapper;
  private final ApiResponseBuilder builder;
  private final UserService userService;

  @Override
  public ResponseEntity<SamplePackResponse> createSamplePack(String authorization,
      MultipartFile file, String title, String description, Double price, Boolean enablePreviews)
      throws Exception {
    SamplePackResponse response = mapper.toSamplePackResponse(
        builder.buildSuccessApiResponse(Constants.CREATE_SAMPLE_PACK_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    response.data(mapper.toSamplePackDto(
        samplePackService.createSamplePack(file, title, description, price, user, enablePreviews)));
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<AudioSampleResponse> getPackPreviews(String packId) throws Exception {
    AudioSampleResponse response = mapper.toAudioSampleResponse(
        builder.buildSuccessApiResponse(Constants.GET_SAMPLE_PACK_PREVIEW_SUCCESS_MESSAGE));
    response.data(mapper.toAudioSampleDtoList(samplePackService.getPackPreviews(packId)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<PublishedSamplePackResponse> getPublishedPacks(String authorization,
      Integer page, Integer size) throws Exception {
    PublishedSamplePackResponse response = mapper.toPublishedSamplePackResponse(
        builder.buildSuccessApiResponse(Constants.GET_SAMPLE_PACK_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    response
        .data(mapper.toSamplePackDtoList(samplePackService.getPublishedPacks(page, size, user)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<TrackBaseApiResponse> publishPack(String packId) throws Exception {
    TrackBaseApiResponse response = mapper.toTrackBaseApiResponse(
        builder.buildSuccessApiResponse(Constants.SAMPLE_PACK_PUBLISH_SUCCESS_MESSAGE));
    samplePackService.publishPack(packId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
