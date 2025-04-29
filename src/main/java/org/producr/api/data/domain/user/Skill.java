package org.producr.api.data.domain.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "skills")
public class Skill implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "skill_id")
  private String id;

  @Column(nullable = false, unique = true, length = 50)
  private String name;

  @Column(length = 50)
  private String category;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "icon_url")
  private String iconUrl;

  @ManyToMany(mappedBy = "skills")
  private Set<User> users = new HashSet<>();

}
