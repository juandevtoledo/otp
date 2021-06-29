package com.lulobank.otp.services.features.otpcardlesswithdrawal;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.otp.sdk.dto.otpcardlesswithdrawal.OtpCardlessWithdrawalRequest;
import com.lulobank.otp.sdk.dto.otpcardlesswithdrawal.OtpCardlessWithdrawalResponse;
import com.lulobank.otp.services.features.onboardingotp.CreateOtpHandler;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.utils.ErrorsOTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpCardlessWithdrawalHandler
  implements Handler<Response<OtpCardlessWithdrawalResponse>, OtpCardlessWithdrawalRequest> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CreateOtpHandler.class);

  @Override
  public Response<OtpCardlessWithdrawalResponse> handle(OtpCardlessWithdrawalRequest command) {
    try {
      OtpService otp = OtpService.getInstance();
      String otpNumber = otp.createOtpCardlessWithdrawal();
      return new Response<>(new OtpCardlessWithdrawalResponse(otpNumber));
    } catch (Exception e) {
      LOGGER.error(ErrorsOTP.SERVICE_ERROR, e);
      return new Response<>(ValidatorUtils.getListValidations(ErrorsOTP.SERVICE_ERROR, e.getMessage()));
    }
  }
}
