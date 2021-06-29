package com.lulobank.otp.sdk.dto.external;

import lombok.Getter;

@Getter
public class VerifyExternalOperationResponse {
  private final boolean success;

  public VerifyExternalOperationResponse(boolean success) {
    this.success = success;
  }
}
