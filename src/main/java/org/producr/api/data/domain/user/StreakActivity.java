package org.producr.api.data.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.producr.api.utils.enums.ActivityType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "streak_activities")
public class StreakActivity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "streak_activity_id")
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_streak_id", nullable = false)
  private UserStreak userStreak;

  @Enumerated(EnumType.STRING)
  @Column(name = "activity_type", nullable = false)
  private ActivityType activityType;

  @Column(name = "activity_details", columnDefinition = "TEXT")
  private String activityDetails;

  @Column(name = "activity_date", nullable = false)
  private LocalDate activityDate;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  // Standard getters, setters, equals, and hashCode methods
}
