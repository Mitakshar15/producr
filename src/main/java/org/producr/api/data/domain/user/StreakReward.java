package org.producr.api.data.domain.user;

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
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.producr.api.utils.enums.RewardType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "streak_rewards")
public class StreakReward implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "streak_reward_id")
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_streak_id", nullable = false)
  private UserStreak userStreak;

  @Column(name = "milestone", nullable = false)
  private Integer milestone;

  @Enumerated(EnumType.STRING)
  @Column(name = "reward_type", nullable = false)
  private RewardType rewardType;

  @Column(name = "reward_value", nullable = false)
  private String rewardValue;

  @Column(name = "awarded_at", nullable = false)
  private LocalDateTime awardedAt;

  @Column(name = "claimed")
  private boolean claimed = false;

  @Column(name = "claimed_at")
  private LocalDateTime claimedAt;

  // Standard getters, setters, equals, and hashCode methods
}
