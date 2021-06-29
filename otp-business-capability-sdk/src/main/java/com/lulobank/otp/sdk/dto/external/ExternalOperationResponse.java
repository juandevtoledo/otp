package com.lulobank.otp.sdk.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalOperationResponse {
  private final String otp;
  private final long expires;

  public ExternalOperationResponse(String otp, long expires) {
    this.otp = otp;
    this.expires = expires;
  }
}
