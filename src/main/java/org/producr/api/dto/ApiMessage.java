package org.producr.api.dto;

import lombok.Data;

@Data
public class ApiMessage {

  private String fieldName;

  private MessageType messageType;

  private ErrorType errorType;

  private String messageKey;

  private String value;

  public ApiMessage fieldName(String fieldName) {
    this.fieldName = fieldName;
    return this;
  }

  public ApiMessage messageType(MessageType messageType) {
    this.messageType = messageType;
    return this;
  }

  public ApiMessage errorType(ErrorType errorType) {
    this.errorType = errorType;
    return this;
  }

  public ApiMessage messageKey(String messageKey) {
    this.messageKey = messageKey;
    return this;
  }

  public ApiMessage value(String value) {
    this.value = value;
    return this;
  }


}
