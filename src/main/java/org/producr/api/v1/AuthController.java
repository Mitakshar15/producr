package org.producr.api.v1;

import lombok.RequiredArgsConstructor;
import org.producr.api.AuthControllerV1Api;
import org.producr.api.builder.ApiResponseBuilder;
import org.producr.api.config.security.jwt.JwtTokenUtil;
import org.producr.api.data.repository.UserRepository;
import org.producr.api.dtos.AuthResponse;
import org.producr.api.dtos.SignInRequest;
import org.producr.api.dtos.SignUpRequest;
import org.producr.api.mapper.UserMgmtMapper;
import org.producr.api.service.interfaces.UserService;
import org.producr.api.utils.constants.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerV1Api {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserMgmtMapper mapper;
  private final ApiResponseBuilder builder;
  private final UserService userService;

  @Override
  public ResponseEntity<AuthResponse> signIn(SignInRequest signInRequest) throws Exception {
    AuthResponse authResponse =
        mapper.toAuthResponse(builder.buildSuccessApiResponse(Constants.SIGN_IN_SUCCESS_MESSAGE));
    authResponse.setData(userService.handleSignIn(signInRequest));
    return new ResponseEntity<>(authResponse, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<AuthResponse> signUp(SignUpRequest signUpRequest) throws Exception {
    AuthResponse response =
        mapper.toAuthResponse(builder.buildSuccessApiResponse(Constants.SIGN_UP_SUCCESS_MESSAGE));
    response.setData(userService.handleSignUp(signUpRequest));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
