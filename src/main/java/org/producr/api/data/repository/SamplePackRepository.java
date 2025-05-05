package org.producr.api.data.repository;

import org.producr.api.data.domain.pack.SamplePack;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SamplePackRepository extends JpaRepository<SamplePack, String> {
  List<SamplePack> findByProducerId(String producerId);

  @Query("select s from SamplePack s where s.isPublished=true")
  List<SamplePack> findByPublished(Pageable pageable);

  List<SamplePack> findByTitleContainingIgnoreCaseAndIsPublishedTrue(String title,
      Pageable pageable);
}
