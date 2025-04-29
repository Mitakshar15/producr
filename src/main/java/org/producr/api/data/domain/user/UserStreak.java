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
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_streaks")
public class UserStreak implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "user_streak_id")
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "current_streak_days")
  private Integer currentStreakDays = 0;

  @Column(name = "longest_streak_days")
  private Integer longestStreakDays = 0;

  @Column(name = "total_active_days")
  private Integer totalActiveDays = 0;

  @Column(name = "streak_start_date")
  private LocalDate streakStartDate;

  @Column(name = "last_activity_date")
  private LocalDate lastActivityDate;

  @Column(name = "streak_freezes_available")
  private Integer streakFreezesAvailable = 0;

  @Column(name = "streak_freeze_used_date")
  private LocalDate streakFreezeUsedDate;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "userStreak", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<StreakActivity> activities = new ArrayList<>();

  @OneToMany(mappedBy = "userStreak", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<StreakReward> rewards = new ArrayList<>();

}
