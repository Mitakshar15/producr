package org.producr.api.data.repository;

import org.producr.api.data.domain.track.Track;
import org.producr.api.data.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, String> {

  @Query("SELECT t from Track t where t.isPublic=true")
  Page<Track> findByPublicTrue(Pageable pageable);


  Optional<List<Track>> findAllByProducer(User producer);
}
