package com.lulobank.otp.services.features.ivr;

import com.amazonaws.SdkClientException;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrResponse;
import com.lulobank.otp.services.outbounadadapters.model.OtpIvrRedisEntity;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpIvrRepository;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.services.utils.LogMessages;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Slf4j
public class ValidateAuthorizationIvrHandler implements Handler<Response<ValidateAuthorizationIvrResponse>, ValidateAuthorizationIvrRequest> {


    private OtpIvrRepository otpIvrRepository;

    public ValidateAuthorizationIvrHandler(OtpIvrRepository otpIvrRepository) {
        this.otpIvrRepository = otpIvrRepository;
    }

    @Override
    public Response<ValidateAuthorizationIvrResponse> handle(ValidateAuthorizationIvrRequest validateAuthorizationIvrRequest) {
        return Try.of(()-> Option.of(validateOTP(validateAuthorizationIvrRequest))
                .filter(productNumber->!productNumber.equals(Strings.EMPTY))
                .map(productNumber-> new Response<>(new ValidateAuthorizationIvrResponse(productNumber)))
                .getOrElse(()-> new Response<>(
                        ValidatorUtils.getListValidations(ErrorsOTP.INVALID_OTP,
                                String.valueOf(HttpStatus.FORBIDDEN.value()))))
        ).recover(SdkClientException.class, e-> new Response<>(ValidatorUtils.getListValidations(ErrorsOTP.OTP_VALIDATION_ERROR,
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())))).get();
    }

    private String validateOTP(@NotNull ValidateAuthorizationIvrRequest validateAuthorizationIvrRequest) {

        String otpRedisEntityId = new StringBuilder(validateAuthorizationIvrRequest.getDocumentNumber()).append(validateAuthorizationIvrRequest.getTransactionType())
                .append(validateAuthorizationIvrRequest.getProductNumber()).toString();
        Optional<OtpIvrRedisEntity> otpToken = otpIvrRepository.findById(otpRedisEntityId);
        return otpToken.map(otp -> {
            String response = "";
            log.debug(LogMessages.OTP_VALIDATION.getMessage(), validateAuthorizationIvrRequest.getOtp(), otp.getOtp());
            if (otp.getOtp().equals(validateAuthorizationIvrRequest.getOtp())) {
                response = otp.getProductNumber();
                otpIvrRepository.delete(otpToken.get());
            }
            return response;
        }).orElse(Strings.EMPTY);
    }
}
