package org.producr.api.data.repository;

import org.producr.api.data.domain.track.AudioSample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<AudioSample, String> {

}
