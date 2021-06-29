package com.lulobank.otp.services.features.onboardingotp.validators;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.otp.services.features.onboardingotp.model.OtpEmailRequest;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.services.utils.OtpUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OtpEmailRequestValidator implements Validator<OtpEmailRequest> {
  @Override
  public ValidationResult validate(OtpEmailRequest otpEmailRequest) {
    try {
      OtpUtils.validateEmail(otpEmailRequest.getEmailTemplate().getTo().get(0));
      OtpUtils.validateOtpFormat(otpEmailRequest.getOtp());
    } catch (Exception e) {
      return new ValidationResult(ErrorsOTP.WRONG_FORMAT_OTP, String.valueOf(otpEmailRequest.getOtp()));
    }
    return null;
  }
}
