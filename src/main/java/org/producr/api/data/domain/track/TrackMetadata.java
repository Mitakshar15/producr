package org.producr.api.data.domain.track;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "track_metadata")
public class TrackMetadata implements Serializable {

  @Id
  @Column(name = "metadata_id")
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "track_id")
  private Track track;

  @Column(name = "bpm")
  private Integer bpm;

  @Column(name = "key")
  private String key;

  @Column(name = "genre", length = 50)
  private String genre;

  @Column(name = "mood", length = 50)
  private String mood;

  @ElementCollection
  @CollectionTable(name = "track_instruments",
      joinColumns = @JoinColumn(name = "track_metadata_id"))
  @Column(name = "instrument")
  private Set<String> instruments = new HashSet<>();

  @Column(name = "is_loop")
  private boolean isLoop = false;

  @Column(name = "is_stem")
  private boolean isStem = false;

  @Column(name = "sample_rate")
  private Integer sampleRate;

  @Column(name = "bit_depth")
  private Integer bitDepth;

  @Column(name = "file_format", length = 10)
  private String fileFormat;

  @Column(name = "file_size_bytes")
  private Long fileSizeBytes;

  // Getters, setters, equals, hashCode
}
