package com.lulobank.otp.services.features.onboardingotp;

import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.features.onboardingotp.model.OtpEmailRequest;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.utils.ErrorsOTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class VerifyOtpEmailHandler implements Handler<Response<OtpResponse>, OtpEmailRequest> {
  private final Logger LOGGER = LoggerFactory.getLogger(VerifyOtpEmailHandler.class);
  private OtpRepository otpRepository;
  private RetrofitClientOperations retrofitClientOperations;

  public VerifyOtpEmailHandler(OtpRepository otpRepository, RetrofitClientOperations retrofitClientOperations) {
    this.otpRepository = otpRepository;
    this.retrofitClientOperations = retrofitClientOperations;
  }

  @Override
  public Response<OtpResponse> handle(OtpEmailRequest command) {
    try {
      if (!validateOTP(command)) {
        return new Response<>(ValidatorUtils.getListValidations(ErrorsOTP.INVALID_OTP, ErrorsOTP.INVALID_OTP));
      }
      retrofitClientOperations.verifyEmailClientInformation(command.getAuthorizationHeader(), command.getEmailTemplate().getTo().get(0));
      return new Response<>(new OtpResponse());
    } catch (Exception e) {
      return new Response<>(ValidatorUtils.getListValidations(ErrorsOTP.SERVICE_ERROR, e.getMessage()));
    }
  }

  private boolean validateOTP(OtpEmailRequest otpEmailRequest) {
    Optional<OtpRedisEntity> otpRedisEntity = otpRepository.findById(otpEmailRequest.getEmailTemplate().getTo().get(0));
    return otpRedisEntity.map(otp -> {
      boolean response = false;
      LOGGER.debug("Otp received: %s Otp verify: %s", otpEmailRequest.getOtp(), otp);
      if (otp.getOtp().equals(otpEmailRequest.getOtp())) {
        otpRepository.delete(otpRedisEntity.get());
        response = true;
      }
      return response;
    }).orElse(Boolean.FALSE);
  }
}
