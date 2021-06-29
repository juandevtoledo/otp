package com.lulobank.otp.services.features.otp;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.otp.sdk.dto.external.VerifyExternalOperation;
import com.lulobank.otp.sdk.dto.external.VerifyExternalOperationResponse;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.utils.OperationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static com.lulobank.otp.services.utils.ErrorsOTP.OTP_VALIDATION_ERROR;

public class VerifyExternalOperationHandler
  implements Handler<Response<VerifyExternalOperationResponse>, VerifyExternalOperation> {
  private static final Logger logger = LoggerFactory.getLogger(VerifyExternalOperationHandler.class);
  private final OtpRepository otpRepository;

  public VerifyExternalOperationHandler(OtpRepository otpRepository) {
    this.otpRepository = otpRepository;
  }

  @Override
  public Response<VerifyExternalOperationResponse> handle(VerifyExternalOperation verifyExternalOperation) {
    try {
      Optional<OtpRedisEntity> otpOptionalEntity = otpRepository.findById(
        OperationUtils.getOperationHash(verifyExternalOperation.findOperation()));
      if (otpOptionalEntity.isPresent() && verifyExternalOperation.getOtp().equals(otpOptionalEntity.get().getOtp())) {
        otpRepository.delete(otpOptionalEntity.get());
        return new Response<>(new VerifyExternalOperationResponse(true));
      } else {
        throw new IllegalStateException(OTP_VALIDATION_ERROR);
      }
    } catch (Exception e) {
      logger.error("error validating otp", e);
      return new Response<>(
        getListValidations(OTP_VALIDATION_ERROR, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
  }
}
