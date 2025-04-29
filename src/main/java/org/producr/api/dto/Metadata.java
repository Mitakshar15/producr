package org.producr.api.dto;


import java.time.Instant;
import java.util.Objects;

public class Metadata {

  private Instant timestamp = null;

  private String traceId;

  public Metadata timestamp(Instant tiemstamp) {
    this.timestamp = tiemstamp;
    return this;
  }

  public Metadata traceId(String traceId) {
    this.traceId = traceId;
    return this;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Metadata metadata = (Metadata) o;
    return Objects.equals(this.timestamp, metadata.timestamp)
        && Objects.equals(this.traceId, metadata.traceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, traceId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Metadata {\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    traceId: ").append(toIndentedString(traceId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

