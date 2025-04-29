package org.producr.api.service.interfaces;

import org.producr.api.data.domain.user.User;
import org.producr.api.dtos.AuthResponseDto;
import org.producr.api.dtos.SignInRequest;
import org.producr.api.dtos.SignUpRequest;

public interface UserService {
  AuthResponseDto handleSignIn(SignInRequest signInRequest);

  AuthResponseDto handleSignUp(SignUpRequest signUpRequest);

  User handleGetUserProfile(String authorization);
}
