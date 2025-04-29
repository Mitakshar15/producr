package org.producr.api.data.repository;

import org.producr.api.data.domain.user.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStreakRepository extends JpaRepository<UserStreak, String> {
}
