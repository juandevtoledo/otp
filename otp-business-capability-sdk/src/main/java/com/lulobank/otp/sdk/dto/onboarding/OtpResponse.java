package com.lulobank.otp.sdk.dto.onboarding;

import com.lulobank.core.helpers.GlobalConstants;
import lombok.Getter;

@Getter
public class OtpResponse {
  private final String otp;

  public OtpResponse(String otpNumber) {
    this.otp = otpNumber;
  }

  public OtpResponse() {
    this.otp = GlobalConstants.EMPTY_STRING;
  }
}
