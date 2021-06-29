package com.lulobank.otp.services.features.onboardingotp.validators;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.otp.services.features.onboardingotp.model.OtpEmailRequest;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.services.utils.OtpUtils;

public class EmailValidator implements Validator<OtpEmailRequest> {
  @Override
  public ValidationResult validate(OtpEmailRequest otpEmailRequest) {
    try {
      OtpUtils.validateEmail(otpEmailRequest.getEmailTemplate().getTo().get(0));
    } catch (Exception e) {
      return new ValidationResult(ErrorsOTP.WRONG_FORMAT_EMAIL, otpEmailRequest.getEmailTemplate().getTo().get(0));
    }
    return null;
  }
}
