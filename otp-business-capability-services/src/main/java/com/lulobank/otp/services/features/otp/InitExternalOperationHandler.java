package com.lulobank.otp.services.features.otp;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.otp.sdk.dto.external.AbstractExternalOperation;
import com.lulobank.otp.sdk.dto.external.ExternalOperationResponse;
import com.lulobank.otp.services.actions.OtpOperationMessage;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.utils.OtpRedisEntityUtils;
import com.lulobank.utils.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static com.lulobank.otp.services.utils.ErrorsOTP.SERVICE_ERROR;

public class InitExternalOperationHandler implements Handler<Response<ExternalOperationResponse>, AbstractExternalOperation> {
  private static final Logger logger = LoggerFactory.getLogger(InitExternalOperationHandler.class);
  private final OtpRepository otpRepository;
  private final OtpService otpService;
  private final Map<String, OtpOperationMessage> operationMessageMap;

  public InitExternalOperationHandler(OtpRepository otpRepository, OtpService otpService,
                                      Map<String, OtpOperationMessage> operationMessageMap) {
    this.otpRepository = otpRepository;
    this.otpService = otpService;
    this.operationMessageMap = operationMessageMap;
  }

  @Override
  public Response<ExternalOperationResponse> handle(AbstractExternalOperation abstractExternalOperation) {
    try {
      int otpLength = this.operationMessageMap.get(abstractExternalOperation.getOperationId()).getLength();
      String otpNumber = this.otpService.create(otpLength);
      OtpRedisEntity otpRedisEntity = OtpRedisEntityUtils
                                        .getOtpRedisEntityFromAbstractExternalOperation(abstractExternalOperation,
                                          otpNumber,
                                          operationMessageMap.get(abstractExternalOperation.getOperationId())
                                            .getExpires());
      this.otpRepository.save(otpRedisEntity);
      return new Response<>(new ExternalOperationResponse(otpRedisEntity.getOtp(), otpRedisEntity.getExpiration()));
    } catch (ServiceException e) {
      logger.error("error generating otp", e);
      return new Response<>(getListValidations(SERVICE_ERROR, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
  }
}
