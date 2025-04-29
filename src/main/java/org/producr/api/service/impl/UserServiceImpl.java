package org.producr.api.service.impl;

import lombok.RequiredArgsConstructor;
import org.producr.api.builder.ApiResponseBuilder;
import org.producr.api.config.security.jwt.JwtTokenUtil;
import org.producr.api.config.security.jwt.UserPrincipal;
import org.producr.api.data.domain.user.User;
import org.producr.api.data.domain.user.UserProfile;
import org.producr.api.data.repository.UserRepository;
import org.producr.api.dtos.AuthResponseDto;
import org.producr.api.dtos.SignInRequest;
import org.producr.api.dtos.SignUpRequest;
import org.producr.api.mapper.UserMgmtMapper;
import org.producr.api.service.interfaces.UserService;
import org.producr.api.utils.enums.AuthProvider;
import org.producr.api.utils.enums.UserRole;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserRepository userRepository;
  private final ApiResponseBuilder builder;
  private final UserMgmtMapper mapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public AuthResponseDto handleSignIn(SignInRequest signInRequest) {
    Authentication authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            signInRequest.getEmail(), signInRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    String jwt = jwtTokenUtil.generateToken(userPrincipal);
    User user = userRepository.findById(userPrincipal.getId()).get();
    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);
    AuthResponseDto authResponseDto = new AuthResponseDto();
    authResponseDto.setToken(jwt);
    authResponseDto.setUserName(user.getUsername());
    authResponseDto.setProvider(user.getProvider().toString());
    return authResponseDto;
  }

  @Override
  public AuthResponseDto handleSignUp(SignUpRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUserName())) {
      throw new RuntimeException("UserName already in use"); // Change it to Custom Exception
    }
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new RuntimeException("Email already in use");
    }

    // Create New User
    User user = new User();
    user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
    user.setProvider(AuthProvider.LOCAL);
    user.setRole(UserRole.USER);
    user.setUsername(signUpRequest.getUserName());
    user.setEmail(signUpRequest.getEmail());
    // For demo purposes, set account as verified In production, you would implement email
    // verification logic
    user.setAccountVerified(true);

    // Create User Profile
    UserProfile profile = new UserProfile();
    profile.setUser(user);
    profile.setDisplayName(signUpRequest.getUserName());
    user.setProfile(profile);

    User savedUser = userRepository.save(user);

    // Generate Jwt for immediate login
    String jwt = jwtTokenUtil.generateTokenFromUser(savedUser);
    AuthResponseDto authResponseDto = new AuthResponseDto();
    authResponseDto.setToken(jwt);
    authResponseDto.setUserName(user.getUsername());
    authResponseDto.setProvider(user.getProvider().toString());
    return authResponseDto;
  }
}
