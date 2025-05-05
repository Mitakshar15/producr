package org.producr.api.data.domain.pack;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.producr.api.utils.enums.SampleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pack_samples")
public class PackSample {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "pack_sample_id")
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pack_id", nullable = false)
  private SamplePack samplePack;

  @Column(nullable = false, length = 100)
  private String filename;

  @Column(name = "audio_file_url", nullable = false)
  private String audioFileUrl;

  @Column(name = "preview_url")
  private String previewUrl;

  @Column(name = "file_size_bytes")
  private Long fileSizeBytes;

  @Column(name = "duration_seconds")
  private Integer durationSeconds;

  @Column(name = "sample_type")
  @Enumerated(EnumType.STRING)
  private SampleType sampleType;

  @Column(name = "bpm")
  private Integer bpm;

  @Column(name = "key")
  private String key;

  @Column(name = "is_preview_sample")
  private boolean isPreviewSample = false;
}
