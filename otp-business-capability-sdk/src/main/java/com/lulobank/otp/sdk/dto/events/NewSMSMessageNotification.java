package com.lulobank.otp.sdk.dto.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewSMSMessageNotification {
  private String phonePrefix;
  private String phoneNumber;
  private String message;
}
