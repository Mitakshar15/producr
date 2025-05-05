package org.producr.api.v1;

import lombok.RequiredArgsConstructor;
import org.producr.api.SamplePacksApi;
import org.producr.api.builder.ApiResponseBuilder;
import org.producr.api.data.domain.user.User;
import org.producr.api.dtos.AudioSampleResponse;
import org.producr.api.dtos.SamplePackResponse;
import org.producr.api.mapper.TrackMgmtMapper;
import org.producr.api.service.SamplePackService;
import org.producr.api.service.interfaces.UserService;
import org.producr.api.utils.constants.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Validated
public class SamplePackController implements SamplePacksApi {
  private final SamplePackService samplePackService;
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
    response.data(mapper.toSamplePackDto(samplePackService.createSamplePack(file, title,
        description, price, user.getId(), enablePreviews)));
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Override
  public ResponseEntity<List<AudioSampleResponse>> getPackPreviews(String packId) throws Exception {
    return SamplePacksApi.super.getPackPreviews(packId);
  }

  @Override
  public ResponseEntity<List<SamplePackResponse>> getPublishedPacks(Integer page, Integer size)
      throws Exception {
    return SamplePacksApi.super.getPublishedPacks(page, size);
  }

  @Override
  public ResponseEntity<Void> publishPack(String packId) throws Exception {
    return SamplePacksApi.super.publishPack(packId);
  }
}
