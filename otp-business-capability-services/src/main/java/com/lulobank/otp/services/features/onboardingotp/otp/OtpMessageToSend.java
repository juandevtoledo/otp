package com.lulobank.otp.services.features.onboardingotp.otp;

import com.lulobank.otp.services.outbounadadapters.services.IMessageToSend;
import lombok.Getter;

@Getter
public class OtpMessageToSend implements IMessageToSend {
  private String phone;
  private String message;

  public OtpMessageToSend(String phone, String message) {
    this.phone = phone;
    this.message = message;
  }
}
