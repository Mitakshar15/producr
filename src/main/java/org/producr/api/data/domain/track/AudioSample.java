package org.producr.api.data.domain.track;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.producr.api.utils.enums.AudioCategory;
import org.producr.api.utils.enums.SampleType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

  @Column(name = "loop_bars")
  private Integer loopBars;

  @Column(name = "tempo_locked")
  private boolean tempoLocked = true;

  @OneToMany(mappedBy = "sample")
  private List<SampleLicense> licenses;

  @Column(name = "sample_pack_id", nullable = true)
  private String samplePackId;

  @Column(name = "original_creator_name")
  private String originalCreatorName;

  @Column(name = "original_source")
  private String originalSource;

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

}
