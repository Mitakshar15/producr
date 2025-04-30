package org.producr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackMetadataDTO {
  private String title;
  private String description;
  private boolean isPublic = true;
  private boolean isDownloadable = false;

  // Beat and Sample common fields
  private Integer bpm;
  private String key;

  // Beat-specific fields
  private String timeSignature;
  private List<String> genres;
  private List<String> moods;
  private BigDecimal leasePrice;
  private BigDecimal exclusivePrice;
  private boolean hasVocals;
  private String producerNotes;
  private List<String> similarArtists;

  // Sample-specific fields
  private String sampleType;
  private String rootNote;
  private boolean isRoyaltyFree = true;
  private boolean isLoop = false;
  private Integer loopBars;
  private boolean tempoLocked = true;
  private String audioCategory;
  private String instrumentType;
  private List<String> sampleTags;
  private boolean attributionRequired = false;
  private String attributionText;
  private boolean commercialUseAllowed = true;

  // Getters and setters
  // ...
}
