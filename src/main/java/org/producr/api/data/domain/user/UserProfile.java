package org.producr.api.data.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column;
import jakarta.persistence.MapKeyColumn;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.producr.api.utils.enums.AvailabilityStatus;
import org.producr.api.utils.enums.CollaborationPreference;
import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "profile_id")
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "display_name", length = 100)
  private String displayName;

  @Column(columnDefinition = "TEXT")
  private String bio;

  @Column(length = 100)
  private String location;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @Column(name = "banner_image_url")
  private String bannerImageUrl;

  @Column(name = "website_url")
  private String websiteUrl;

  @ElementCollection
  @CollectionTable(name = "user_social_links", joinColumns = @JoinColumn(name = "profile_id"))
  @MapKeyColumn(name = "platform")
  @Column(name = "url")
  private Map<String, String> socialLinks = new HashMap<>();

  @ElementCollection
  @CollectionTable(name = "user_genre_preferences", joinColumns = @JoinColumn(name = "profile_id"))
  @Column(name = "genre")
  private Set<String> genrePreferences = new HashSet<>();

  @Column(name = "years_experience")
  private Integer yearsExperience;

  @Column(name = "equipment_info", columnDefinition = "TEXT")
  private String equipmentInfo;

  @Column(name = "availability_status")
  @Enumerated(EnumType.STRING)
  private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;

  @Column(name = "collaboration_preference")
  @Enumerated(EnumType.STRING)
  private CollaborationPreference collaborationPreference = CollaborationPreference.OPEN;

  // Getters, setters, equals, hashCode
}
