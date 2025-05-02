package org.producr.api.data.domain.track;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.producr.api.data.domain.user.User;
import org.producr.api.utils.enums.TrackType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tracks")
@Inheritance(strategy = InheritanceType.JOINED)
public class Track implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "track_id")
  private String id;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "producer_id", nullable = false)
  @ToString.Exclude
  private User producer;

  @Column(name = "audio_file_url", nullable = false)
  private String audioFileUrl;

  //Marked for removal
  @Column(name = "waveform_data", columnDefinition = "TEXT")
  private String waveformData;

  @Column(name = "track_length_seconds")
  private Integer trackLengthSeconds;

  @Column(name = "is_public")
  private boolean isPublic = true;

  @Column(name = "is_downloadable")
  private boolean isDownloadable = false;

  @Column(name = "plays_count")
  private Integer playsCount = 0;

  @Column(name = "likes_count")
  private Integer likesCount = 0;

  @Column(name = "comments_count")
  private Integer commentsCount = 0;

  @ManyToMany
  @JoinTable(name = "track_tags", joinColumns = @JoinColumn(name = "track_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private Set<Tag> tags = new HashSet<>();

  @OneToOne(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private TrackMetadata metadata;

  @OneToMany(mappedBy = "track")
  private List<Comment> comments = new ArrayList<>();

  @OneToMany(mappedBy = "track")
  private List<TrackPlay> plays = new ArrayList<>();

  @OneToMany(mappedBy = "track")
  private List<TrackLike> likes = new ArrayList<>();

  @OneToMany(mappedBy = "track")
  private List<TrackVersion> versions = new ArrayList<>();

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  // Discriminator field to identify track type
  @Column(name = "track_type", insertable = false, updatable = false)
  @Enumerated(EnumType.STRING)
  private TrackType trackType;

  public boolean isBeat() {
    return this instanceof Beat;
  }

  public boolean isAudioSample() {
    return this instanceof AudioSample;
  }

  public Beat asBeat() {
    if (isBeat()) {
      return (Beat) this;
    }
    throw new IllegalStateException("Track is not a Beat");
  }

  public AudioSample asAudioSample() {
    if (isAudioSample()) {
      return (AudioSample) this;
    }
    throw new IllegalStateException("Track is not an AudioSample");
  }

  @PrePersist
  void setTrackType() {
    if (this instanceof AudioSample) {
      this.trackType = TrackType.SAMPLE;
    } else if (this instanceof Beat) {
      this.trackType = TrackType.BEAT;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Track track = (Track) o;
    return Objects.equals(id, track.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id); // Use only id to avoid recursion
  }

  public void incrementPlayCount() {
    this.playsCount++;
  }
}
