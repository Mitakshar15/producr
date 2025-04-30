package org.producr.api.v1;

import lombok.RequiredArgsConstructor;
import org.producr.api.UserControllerV1Api;
import org.producr.api.builder.ApiResponseBuilder;
import org.producr.api.dtos.UserProfileResponse;
import org.producr.api.mapper.UserMgmtMapper;
import org.producr.api.service.interfaces.UserService;
import org.producr.api.utils.constants.UserConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerV1Api {

  private final UserMgmtMapper mapper;
  private final ApiResponseBuilder builder;
  private final UserService userService;


  @Override
  public ResponseEntity<UserProfileResponse> getUserProfile(String authorization) throws Exception {
    UserProfileResponse response = mapper.toUserProfileResponse(
        builder.buildSuccessApiResponse(UserConstants.GET_USER_PROFILE_SUCCES_MESSAGE));
    response.data(builder.buildUserProfileData(userService.handleGetUserProfile(authorization)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
