package com.lulobank.otp.services.outbounadadapters.services;

import lombok.Getter;

@Getter
public class NewSMSMessageToSend implements IMessageToSend {
  private final String phone;
  private final String message;

  public NewSMSMessageToSend(String phone, String message) {
    this.phone = phone;
    this.message = message;
  }
}
