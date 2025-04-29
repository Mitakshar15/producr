package org.producr.api.data.domain.track;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.producr.api.data.domain.user.User;
import org.producr.api.utils.enums.BeatLicenseType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@DiscriminatorValue("BEAT")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "beat_licenses")
public class BeatLicense extends LicensingInfo {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "beat_id", nullable = false)
  private Beat beat;

  @Enumerated(EnumType.STRING)
  @Column(name = "license_type", nullable = false)
  private BeatLicenseType beatLicenseType;

  @Column(name = "max_distribution_copies")
  private Integer maxDistributionCopies;

  @Column(name = "allows_commercial_use")
  private boolean allowsCommercialUse;

  @Column(name = "allows_music_videos")
  private boolean allowsMusicVideos;

  @Column(name = "allows_performance_rights")
  private boolean allowsPerformanceRights;


}
