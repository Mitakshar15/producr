package org.producr.api.data.repository;

import org.producr.api.data.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

  Optional<User> findByEmail(String email);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  Optional<User> findByUsername(String username);

  @Query("select u from User u where u.username=:userName or u.email=:userName")
  Optional<User> findByEmailOrUsername(@Param("userName") String userName);
}
