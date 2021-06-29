package com.lulobank.otp.services.features.onboardingotp;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.features.onboardingotp.model.OtpEmailRequest;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.services.utils.OtpRedisEntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOtpEmailHandler implements Handler<Response<OtpResponse>, OtpEmailRequest> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateOtpEmailHandler.class);
  private OtpRepository otpRepository;

  public CreateOtpEmailHandler(OtpRepository otpRepository) {
    this.otpRepository = otpRepository;
  }

  @Override
  public Response<OtpResponse> handle(OtpEmailRequest command) {
    try {
      OtpService otp = OtpService.getInstance();
      String otpNumber = otp.create();
      otpRepository.save(OtpRedisEntityUtils.getOtpRedisEntityFromEmailTemplate(command.getEmailTemplate(), otpNumber));
      return new Response<>(new OtpResponse(otpNumber));
    } catch (Exception e) {
      LOGGER.error("Error OtpResponse: ", e);
      return new Response<>(ValidatorUtils.getListValidations(ErrorsOTP.SERVICE_ERROR, e.getMessage()));
    }
  }
}
