package org.producr.api.data.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.producr.api.data.domain.track.Track;
import org.producr.api.utils.enums.AuthProvider;
import org.producr.api.utils.enums.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "user_id")
  private String id;

  @Column(unique = true, nullable = false, length = 50)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @JsonIgnore
  @Column(nullable = false)
  private String password;

  @Column(name = "account_verified")
  private boolean accountVerified = false;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuthProvider provider = AuthProvider.LOCAL;

  @Column
  private String providerId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserRole role = UserRole.USER;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonManagedReference
  private UserProfile profile;

  @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL)
  private Set<Track> tracks = new HashSet<>();

  @ManyToMany
  @JoinTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "skill_id"))
  private Set<Skill> skills = new HashSet<>();

  @ManyToMany
  @JoinTable(name = "user_badges", joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "badge_id"))
  private Set<Badge> badges = new HashSet<>();

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true,
      fetch = FetchType.LAZY)
  private UserStreak streak;

  @Column(name = "created_at")
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "email_verification_token")
  private String emailVerificationToken;

  @Column(name = "reset_password_token")
  private String resetPasswordToken;

  @Column(name = "reset_password_expiry")
  private LocalDateTime resetPasswordExpiry;

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return Objects.equals(id, user.id); // Use only id for equality
  }

  @Override
  public int hashCode() {
    return Objects.hash(id); // Use only id for hashCode
  }
}
