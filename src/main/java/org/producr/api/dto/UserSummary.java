package org.producr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.producr.api.utils.enums.AuthProvider;
import org.producr.api.utils.enums.UserRole;

@Data
@AllArgsConstructor
public class UserSummary {
  private String id;
  private String username;
  private String email;
  private UserRole role;
  private boolean accountVerified;
  private AuthProvider provider;
}
