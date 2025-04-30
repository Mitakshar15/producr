package org.producr.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioFileInfo {
  private int durationSeconds;
  private String waveformData;
  private String format;
  private int sampleRate;
  private int bitDepth;
  private int channels;

  // Getters and setters
  // ...
}
