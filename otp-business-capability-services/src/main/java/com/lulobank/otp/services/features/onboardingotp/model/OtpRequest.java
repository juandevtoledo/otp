package com.lulobank.otp.services.features.onboardingotp.model;

import com.lulobank.core.Command;
import lombok.Getter;

@Getter
public class OtpRequest implements Command {
  private String prefix;
  private String phoneNumber;
  private String otp;

  public OtpRequest(String prefix, String phoneNumber) {
    this.prefix = prefix;
    this.phoneNumber = phoneNumber;
  }

  public OtpRequest(String prefix, String phoneNumber, String otp) {
    this.prefix = prefix;
    this.phoneNumber = phoneNumber;
    this.otp = otp;
  }
}
