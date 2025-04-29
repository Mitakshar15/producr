package org.producr.api.data.domain.track;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.producr.api.data.domain.user.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "track_plays")
public class TrackPlay implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "track_play_id")
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "track_id", nullable = false)
  private Track track;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;

  @Column(name = "played_at")
  @CreationTimestamp
  private LocalDateTime playedAt;

  @Column(name = "play_duration_seconds")
  private Integer playDurationSeconds;

  // Getters, setters, equals, hashCode
}
