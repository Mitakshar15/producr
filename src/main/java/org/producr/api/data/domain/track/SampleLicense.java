package org.producr.api.data.domain.track;

import jakarta.persistence.*;
import org.producr.api.utils.enums.SampleLicenseType;

@Entity
@DiscriminatorValue("SAMPLE")
public class SampleLicense extends LicensingInfo {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sample_id", nullable = false)
  private AudioSample sample;

  @Enumerated(EnumType.STRING)
  @Column(name = "sample_license_type", nullable = false)
  private SampleLicenseType sampleLicenseType;

  @Column(name = "allows_modification")
  private boolean allowsModification;

  @Column(name = "requires_attribution")
  private boolean requiresAttribution;

  @Column(name = "allows_redistribution")
  private boolean allowsRedistribution;

  @Column(name = "max_use_count")
  private Integer maxUseCount;

}
