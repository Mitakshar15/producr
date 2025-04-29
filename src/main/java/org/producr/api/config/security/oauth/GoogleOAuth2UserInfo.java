package org.producr.api.config.security.oauth;

import lombok.Getter;

import java.util.Map;

@Getter
public class GoogleOAuth2UserInfo {
  private final Map<String, Object> attributes;

  public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  public String getId() {
    return (String) attributes.get("sub");
  }

  public String getName() {
    return (String) attributes.get("name");
  }

  public String getEmail() {
    return (String) attributes.get("email");
  }

  public String getImageUrl() {
    return (String) attributes.get("picture");
  }
}
