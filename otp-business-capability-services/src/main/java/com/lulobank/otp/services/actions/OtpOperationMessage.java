package com.lulobank.otp.services.actions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpOperationMessage {
  private String message;
  private int expires;
  private int length;
}
