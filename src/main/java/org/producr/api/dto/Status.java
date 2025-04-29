package org.producr.api.dto;

public class Status {

  private int statusCode;

  private String statusMessage;

  private String statusMessageKey;

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public String getStatusMessageKey() {
    return statusMessageKey;
  }

  public void setStatusMessageKey(String statusMessageKey) {
    this.statusMessageKey = statusMessageKey;
  }

  public Status statusCode(int value) {
    this.statusCode = value;
    return this;
  }

  public Status statusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
    return this;
  }

  public Status statusMessageKey(String statusMessageKey) {
    this.statusMessageKey = statusMessageKey;
    return this;
  }
}
