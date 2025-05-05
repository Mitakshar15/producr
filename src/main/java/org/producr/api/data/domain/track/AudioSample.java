package org.producr.api.data.domain.track;

import jakarta.persistence.*;
import lombok.*;
import org.producr.api.data.domain.pack.SamplePack;
import org.producr.api.utils.enums.AudioCategory;
import org.producr.api.utils.enums.SampleType;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audio_samples")
@PrimaryKeyJoinColumn(name = "track_id")
public class AudioSample extends Track {

  @Column(name = "sample_type")
  @Enumerated(EnumType.STRING)
  private SampleType sampleType = SampleType.ONE_SHOT;

  @Column(name = "root_note")
  private String rootNote;

  @Column(name = "is_royalty_free")
  private boolean isRoyaltyFree = true;

  @Column(name = "is_loop")
  private boolean isLoop = false;

  @Column(name = "loop_tempo")
  private Integer loopTempo;

  @Column(name = "key")
  private String key;

  @Column(name = "loop_bars")
  private Integer loopBars;

  @Column(name = "tempo_locked")
  private boolean tempoLocked = true;

  @OneToMany(mappedBy = "sample")
  private List<SampleLicense> licenses;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sample_pack_id", nullable = false)
  private SamplePack samplePack;

  @Column(name = "original_creator_name")
  private String originalCreatorName;

  @Column(name = "original_source")
  private String originalSource;

  @Column(name = "file_size_bytes")
  private Long fileSizeBytes;

  @Column(name = "audio_category")
  @Enumerated(EnumType.STRING)
  private AudioCategory audioCategory;

  @Column(name = "instrument_type")
  private String instrumentType;

  @ElementCollection
  @CollectionTable(name = "sample_tags", joinColumns = @JoinColumn(name = "sample_id"))
  @Column(name = "tag")
  private Set<String> sampleTags = new HashSet<>();

  @Column(name = "download_count")
  private Integer downloadCount = 0;

  @Column(name = "usage_count")
  private Integer usageCount = 0;

  @Column(name = "attribution_required")
  private boolean attributionRequired = false;

  @Column(name = "attribution_text", columnDefinition = "TEXT")
  private String attributionText;

  @Column(name = "commercial_use_allowed")
  private boolean commercialUseAllowed = true;

  @Column(name = "prview_enabled")
  private Boolean isPreviewSample = false;

  public AudioSample(Track track) {
    this.setId(track.getId());
    this.setTitle(track.getTitle());
    this.setDescription(track.getDescription());
    this.setProducer(track.getProducer());
    this.setAudioFileUrl(track.getAudioFileUrl());
    this.setWaveformData(track.getWaveformData());
    this.setTrackLengthSeconds(track.getTrackLengthSeconds());
    this.setPublic(track.isPublic());
    this.setDownloadable(true); // Samples are typically downloadable

    // Copy timestamps
    this.setCreatedAt(track.getCreatedAt());
    this.setUpdatedAt(track.getUpdatedAt());

    // Copy collections if needed
    if (track.getTags() != null) {
      this.setTags(new HashSet<>(track.getTags()));
    }

    // Set sample-specific defaults
    this.setRoyaltyFree(true);
    this.setLoop(false);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getTitle());
  }

  public void incrementDownloadCount() {
    this.downloadCount++;
  }
}
