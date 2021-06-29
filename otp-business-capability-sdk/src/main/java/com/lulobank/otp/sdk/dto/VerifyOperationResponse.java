package com.lulobank.otp.sdk.dto;

import lombok.Getter;

@Getter
public class VerifyOperationResponse {
  private final boolean completed;

  public VerifyOperationResponse(boolean completed) {
    this.completed = completed;
  }
}
