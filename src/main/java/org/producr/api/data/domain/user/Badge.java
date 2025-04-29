package org.producr.api.data.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.producr.api.utils.enums.BadgeCategory;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "badges")
public class Badge implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "badge_id")
  private String id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "image_url", nullable = false)
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BadgeCategory category;

  @Column(name = "achievement_criteria", columnDefinition = "TEXT")
  private String achievementCriteria;

  @ManyToMany(mappedBy = "badges")
  private Set<User> users = new HashSet<>();

}
