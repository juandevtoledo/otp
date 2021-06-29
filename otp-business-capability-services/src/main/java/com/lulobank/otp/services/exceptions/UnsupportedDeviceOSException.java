package com.lulobank.otp.services.exceptions;

import java.io.InvalidClassException;

public class UnsupportedDeviceOSException extends InvalidClassException {
  public UnsupportedDeviceOSException(String message) {
    super(message);
  }
}
