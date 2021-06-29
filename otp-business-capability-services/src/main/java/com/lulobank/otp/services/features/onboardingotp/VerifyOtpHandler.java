package com.lulobank.otp.services.features.onboardingotp;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.features.onboardingotp.model.OtpRequest;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.services.utils.OtpRedisEntityUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VerifyOtpHandler implements Handler<Response<OtpResponse>, OtpRequest> {
  private final Logger LOGGER = LoggerFactory.getLogger(VerifyOtpHandler.class);
  private OtpRepository otpRepository;

  public VerifyOtpHandler(OtpRepository otpRepository) {
    this.otpRepository = otpRepository;
  }

  @Override
  public Response<OtpResponse> handle(OtpRequest command) {
    try {
      if (!validateOTP(command)) {
        return new Response<>(getValidationError(new ValidationResult(ErrorsOTP.INVALID_OTP, ErrorsOTP.INVALID_OTP)));
      }
      return new Response<>(new OtpResponse());
    } catch (Exception e) {
      return new Response<>(getValidationError(new ValidationResult(ErrorsOTP.SERVICE_ERROR, e.getMessage())));
    }
  }

  private boolean validateOTP(@NotNull OtpRequest otpRequest) {
    String otpRedisEntityId = OtpRedisEntityUtils.getOtpRedisEntityKeyFromPrefixAndPhoneNumber(otpRequest.getPrefix(),
      otpRequest.getPhoneNumber());
    Optional<OtpRedisEntity> otpToken = otpRepository.findById(otpRedisEntityId);
    return otpToken.map(otp -> {
      boolean response = false;
      LOGGER.debug("Otp received: {} Otp verify: {} ", otpRequest.getOtp(), otp);
      if (otp.getOtp().equals(otpRequest.getOtp())) {
        otpRepository.delete(otpToken.get());
        response = true;
      }
      return response;
    }).orElse(Boolean.FALSE);
  }

  private List<ValidationResult> getValidationError(ValidationResult e) {
    List<ValidationResult> errors = new ArrayList<>();
    errors.add(e);
    return errors;
  }
}
