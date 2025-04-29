package org.producr.api.dto;

public enum ErrorType {

  REQUEST_ERROR("REQUEST_ERROR"), API_ERROR("GENERAL_ERROR");

  private String value;

  ErrorType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
