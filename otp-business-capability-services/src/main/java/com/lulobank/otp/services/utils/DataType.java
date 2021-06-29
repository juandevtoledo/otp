package com.lulobank.otp.services.utils;

public enum DataType {
  STRING("String"), NUMBER("Number");
  private String type;

  DataType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
