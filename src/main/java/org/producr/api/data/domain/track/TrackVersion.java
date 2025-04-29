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


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "track_versions")
public class TrackVersion implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "track_version_id")
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "track_id", nullable = false)
  private Track track;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(name = "version_number")
  private Integer versionNumber;

  @Column(name = "audio_file_url", nullable = false)
  private String audioFileUrl;

  @Column(name = "change_notes", columnDefinition = "TEXT")
  private String changeNotes;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  // Getters, setters, equals, hashCode
}
