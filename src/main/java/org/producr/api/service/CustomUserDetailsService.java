package org.producr.api.service;

import lombok.RequiredArgsConstructor;
import org.producr.api.config.security.jwt.UserPrincipal;
import org.producr.api.data.domain.user.User;
import org.producr.api.data.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    return UserPrincipal.create(user);
  }

  @Transactional
  public UserDetails loadUserById(String id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

    return UserPrincipal.create(user);
  }
}
