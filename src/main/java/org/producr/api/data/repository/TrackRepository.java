package org.producr.api.data.repository;

import org.producr.api.data.domain.track.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, String> {
}
