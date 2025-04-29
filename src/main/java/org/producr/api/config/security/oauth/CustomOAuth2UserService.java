package org.producr.api.config.security.oauth;

import lombok.RequiredArgsConstructor;
import org.producr.api.config.security.jwt.UserPrincipal;
import org.producr.api.data.domain.user.User;
import org.producr.api.data.domain.user.UserProfile;
import org.producr.api.data.repository.UserRepository;
import org.producr.api.utils.enums.AuthProvider;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest)
      throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

    try {
      return processOAuth2User(oAuth2UserRequest, oAuth2User);
    } catch (Exception ex) {
      throw new OAuth2AuthenticationException(ex.getMessage());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

    if (StringUtils.isEmpty(userInfo.getEmail())) {
      throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
    }

    Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
    User user;

    if (userOptional.isPresent()) {
      user = userOptional.get();

      if (user.getProvider() == AuthProvider.LOCAL) {
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(userInfo.getId());
        user = userRepository.save(user);
      }
    } else {
      user = registerNewUser(oAuth2UserRequest, userInfo);
    }

    return UserPrincipal.create(user, oAuth2User.getAttributes());
  }

  private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, GoogleOAuth2UserInfo userInfo) {
    User user = new User();

    user.setProvider(AuthProvider.GOOGLE);
    user.setProviderId(userInfo.getId());
    user.setUsername(userInfo.getName());
    user.setEmail(userInfo.getEmail());
    user.setPassword("");
    user.setAccountVerified(true);

    // Create profile
    UserProfile profile = new UserProfile();
    profile.setUser(user);
    profile.setDisplayName(userInfo.getName());
    profile.setProfileImageUrl(userInfo.getImageUrl());
    user.setProfile(profile);

    return userRepository.save(user);
  }
}
