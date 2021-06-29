package com.lulobank.otp.services.outbounadadapters.services;

import lombok.Getter;

@Getter
public class SMSMessageAttribute {
  private final String senderId;
  private final String maxPrice;
  private final String type;

  public SMSMessageAttribute(final String senderId, final String maxPrice, final String type) {
    this.maxPrice = maxPrice;
    this.senderId = senderId;
    this.type = type;
  }
}
