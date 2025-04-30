package org.producr.api.data.domain.track;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "beats")
@PrimaryKeyJoinColumn(name = "track_id")
public class Beat extends Track {

  @Column(name = "bpm")
  private Integer bpm;

  @Column(name = "key")
  private String key;

  @Column(name = "time_signature")
  private String timeSignature = "4/4";

  @ElementCollection
  @CollectionTable(name = "beat_genres", joinColumns = @JoinColumn(name = "beat_id"))
  @Column(name = "genre")
  private Set<String> genres = new HashSet<>();

  @ElementCollection
  @CollectionTable(name = "beat_moods", joinColumns = @JoinColumn(name = "beat_id"))
  @Column(name = "mood")
  private Set<String> moods = new HashSet<>();

  @Column(name = "is_exclusive", nullable = false)
  private boolean isExclusive = false;

  @Column(name = "has_sold_exclusive")
  private boolean hasSoldExclusive = false;

  @Column(name = "lease_price")
  private BigDecimal leasePrice;

  @Column(name = "exclusive_price")
  private BigDecimal exclusivePrice;

  @Column(name = "stem_available")
  private boolean stemAvailable = false;

  @Column(name = "stem_price")
  private BigDecimal stemPrice;

  @Column(name = "is_featured")
  private boolean isFeatured = false;

  @Column(name = "featured_until")
  private LocalDate featuredUntil;

  @ElementCollection
  @CollectionTable(name = "beat_similar_artists", joinColumns = @JoinColumn(name = "beat_id"))
  @Column(name = "artist_name")
  private Set<String> similarArtists = new HashSet<>();

  @OneToMany(mappedBy = "beat", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<BeatLicense> licenses = new HashSet<>();

  @OneToMany(mappedBy = "beat", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<BeatStem> stems = new HashSet<>();

  @Column(name = "sales_count")
  private Integer salesCount = 0;

  @Column(name = "placement_count")
  private Integer placementCount = 0;

  @Column(name = "has_vocals")
  private boolean hasVocals = false;

  @Column(name = "producer_notes", columnDefinition = "TEXT")
  private String producerNotes;

  @Column(name = "is_verified_original")
  private boolean isVerifiedOriginal = false;


  public Beat(Track track) {
    // Copy base track properties using setters
    this.setId(track.getId());
    this.setTitle(track.getTitle());
    this.setDescription(track.getDescription());
    this.setProducer(track.getProducer());
    this.setAudioFileUrl(track.getAudioFileUrl());
    this.setWaveformData(track.getWaveformData());
    this.setTrackLengthSeconds(track.getTrackLengthSeconds());
    this.setPublic(track.isPublic());
    this.setDownloadable(false); // Beats typically aren't freely downloadable

    // Copy timestamps
    this.setCreatedAt(track.getCreatedAt());
    this.setUpdatedAt(track.getUpdatedAt());

    // Copy collections if needed
    if (track.getTags() != null) {
      this.setTags(new HashSet<>(track.getTags()));
    }
  }
}
