package org.producr.api.data.domain.track;

import jakarta.persistence.*;
import org.producr.api.utils.enums.StemType;

import java.io.Serializable;

@Entity
@Table(name = "beat_stems")
public class BeatStem implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "beat_id", nullable = false)
  private Beat beat;

  @Column(name = "stem_name", nullable = false)
  private String stemName;

  @Column(name = "audio_file_url", nullable = false)
  private String audioFileUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "stem_type")
  private StemType stemType;

  @Column(name = "download_count")
  private Integer downloadCount = 0;

  // Business method
  public void incrementDownloadCount() {
    this.downloadCount++;
  }

  // Getters and Setters
  // ...
}
