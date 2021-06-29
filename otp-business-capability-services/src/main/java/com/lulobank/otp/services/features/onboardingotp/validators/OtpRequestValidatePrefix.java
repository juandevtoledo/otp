package com.lulobank.otp.services.features.onboardingotp.validators;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.otp.services.features.onboardingotp.model.OtpRequest;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.services.utils.OtpUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OtpRequestValidatePrefix implements Validator<OtpRequest> {
  @Override
  public ValidationResult validate(OtpRequest createOtpRequest) {
    try {
      OtpUtils.validatePrefix(createOtpRequest.getPrefix());
    } catch (Exception e) {
      return new ValidationResult(ErrorsOTP.WRONG_PREFIX, createOtpRequest.getPrefix());
    }
    return null;
  }
}

