package com.lulobank.otp.services.features.onboardingotp.validators;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.otp.services.features.onboardingotp.model.OtpRequest;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.services.utils.OtpUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OtpRequestValidateOtpFormat implements Validator<OtpRequest> {
  @Override
  public ValidationResult validate(OtpRequest otpRequest) {
    try {
      OtpUtils.validateOtpFormat(String.valueOf(otpRequest.getOtp()));
    } catch (Exception e) {
      return new ValidationResult(ErrorsOTP.WRONG_FORMAT_OTP, String.valueOf(otpRequest.getOtp()));
    }
    return null;
  }
}

