package org.producr.api.data.repository;

import org.producr.api.data.domain.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {

}
