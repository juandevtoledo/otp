package com.lulobank.otp.services.exceptions;

import lombok.Getter;

@Getter
public class OtpException extends Exception {
  private final int codeError;
  private final String messageError;

  public OtpException(Throwable throwable, int codeError, String messageError) {
    super(throwable);
    this.codeError = codeError;
    this.messageError = messageError;
  }
}
