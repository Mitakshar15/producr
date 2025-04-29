package org.producr.api.data.domain.track;

import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.producr.api.data.domain.user.User;
import org.producr.api.utils.enums.LicenseType;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "licensing_info")
@Inheritance(strategy = InheritanceType.JOINED)
public class LicensingInfo implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "licensing_info_id")
  private String id;

  @Enumerated(EnumType.STRING)
  @Column(name = "license_type", nullable = false)
  private LicenseType licenseType = LicenseType.ALL_RIGHTS_RESERVED;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "licensee_id", nullable = false)
  private User licensee;

  @Column(name = "is_available_for_licensing")
  private boolean isAvailableForLicensing = false;

  @Column(name = "transaction_id")
  private String transactionId;

  @Column(name = "licensing_price")
  private BigDecimal licensingPrice;

  @Column(name = "license_terms", columnDefinition = "TEXT")
  private String licenseTerms;

  @Column(name = "purchase_price", nullable = false)
  private BigDecimal purchasePrice;

  @Column(name = "purchase_date", nullable = false)
  private LocalDateTime purchaseDate;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = true;

  @Column(name = "download_count")
  private Integer downloadCount = 0;

  @Column(name = "last_download_date")
  private LocalDateTime lastDownloadDate;

  @Column(name = "expiration_date")
  private LocalDateTime expirationDate;

}
