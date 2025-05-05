package org.producr.api.data.domain.pack;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.producr.api.data.domain.track.AudioSample;
import org.producr.api.data.domain.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sample_packs")
public class SamplePack implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "pack_id")
  private String id;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "producer_id", nullable = false)
  @ToString.Exclude
  private User producer;

  @Column(name = "zip_file_url", nullable = false)
  private String zipFileUrl;

  @Column(name = "cover_image_url")
  private String coverImageUrl;

  @Column(name = "preview_enabled")
  private boolean previewEnabled = false;

  @OneToMany(mappedBy = "samplePack", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<AudioSample> samples = new HashSet<>(); //Preview Samples

  @Column(name = "price")
  private Double price;

  @Column(name = "total_size_bytes")
  private Long totalSizeBytes;

  @Column(name = "sample_count")
  private Integer sampleCount = 0;

  @Column(name = "download_count")
  private Integer downloadCount = 0;

  @Column(name = "is_published")
  private boolean isPublished = false;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // Business methods
  public void incrementDownloadCount() {
    this.downloadCount++;
  }

  public void addSample(AudioSample sample) {
    samples.add(sample);
    sample.setSamplePack(this);
    this.sampleCount++;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public void removeSample(PackSample sample) {
    samples.remove(sample);
    sample.setSamplePack(null);
    this.sampleCount--;
  }



}
