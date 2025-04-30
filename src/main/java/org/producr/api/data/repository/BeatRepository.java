package org.producr.api.data.repository;

import org.producr.api.data.domain.track.Beat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeatRepository extends JpaRepository<Beat, String> {
}
