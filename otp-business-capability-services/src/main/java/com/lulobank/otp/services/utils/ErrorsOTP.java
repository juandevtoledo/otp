package com.lulobank.otp.services.utils;

public final class ErrorsOTP {
  public static final String WRONG_PREFIX = "Wrong prefix format";
  public static final String WRONG_FORMAT_PHONE_NUMBER = "Wrong phone number format";
  public static final String WRONG_FORMAT_OTP = "Wrong OTP format";
  public static final String WRONG_FORMAT_EMAIL = "Wrong Email format";
  public static final String SERVICE_ERROR = "Service error";
  public static final String OTP_VALIDATION_ERROR = "Error validating otp with this client";
  public static final String OTP_GENERATION_ERROR = "Error generating otp";
  public static final String INVALID_OTP = "Invalid OTP";
  public static final String BAD_REQUEST = "BAD_REQUEST";
  public static final String OTP_SERVICE_ERROR = "OTP service error";
  public static final String INVALID_SAC_TRANSACTION_TYPE = "Invalid sac transaction type";


  private ErrorsOTP() {
    throw new IllegalStateException("Utility class");
  }
}
