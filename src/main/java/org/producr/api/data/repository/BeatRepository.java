package org.producr.api.data.repository;

import org.producr.api.data.domain.track.Beat;
import org.producr.api.data.domain.track.Track;
import org.producr.api.data.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeatRepository extends JpaRepository<Beat, String> {
  Optional<List<Track>> findByProducer(User producer);
}
