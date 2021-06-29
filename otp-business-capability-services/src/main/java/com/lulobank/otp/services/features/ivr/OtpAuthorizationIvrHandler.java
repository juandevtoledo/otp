package com.lulobank.otp.services.features.ivr;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrResponse;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpIvrRepository;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.services.utils.OtpRedisEntityUtils;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.http.HttpStatus;

import java.util.function.Function;

@Slf4j
public class OtpAuthorizationIvrHandler implements Handler<Response<AuthorizationIvrResponse>, AuthorizationIvrRequest> {

    private OtpIvrRepository otpIvrRepository;

    public OtpAuthorizationIvrHandler(OtpIvrRepository otpIvrRepository) {
        this.otpIvrRepository = otpIvrRepository;
    }

    @Override
    public Response<AuthorizationIvrResponse> handle(AuthorizationIvrRequest authorizationIvrRequest) {

        OtpService otp = OtpService.getInstance();
        return Try.of(otp::create)
                .map(getAuthorizationIvrOtp(authorizationIvrRequest))
                .recover(Exception.class, e -> handleException(e, authorizationIvrRequest.getDocumentNumber())).get();
    }

    public Function<String, Response<AuthorizationIvrResponse>> getAuthorizationIvrOtp(AuthorizationIvrRequest authorizationIvrRequest){
        return otpNumber ->
        {
            AuthorizationIvrResponse authorizationIvrResponse = new AuthorizationIvrResponse();
            authorizationIvrResponse.setOtp(otpNumber);
            otpIvrRepository.save(
                    OtpRedisEntityUtils.
                            getOtpRedisEntityFromAuthorizationIvrRequest(authorizationIvrRequest, otpNumber));
            return new Response<>(authorizationIvrResponse);
        };
    }

    public Response<AuthorizationIvrResponse> handleException(Exception ex, String infoClient) {
        log.error(
                "Otp service error while getting otp, infoClient: {}, Message: {}, Error: {}",
                Encode.forJava(infoClient),
                ex.getCause(),
                ex);
        return new Response<>(ValidatorUtils.getListValidations(ErrorsOTP.OTP_SERVICE_ERROR,
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
}
