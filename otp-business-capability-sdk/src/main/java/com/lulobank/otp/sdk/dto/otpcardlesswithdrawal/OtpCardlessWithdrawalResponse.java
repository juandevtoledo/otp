package com.lulobank.otp.sdk.dto.otpcardlesswithdrawal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpCardlessWithdrawalResponse {
  private String otp;

  public OtpCardlessWithdrawalResponse(String otp) {
    this.otp = otp;
  }
}
