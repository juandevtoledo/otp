package com.lulobank.otp.starter.v3.adapters.in.util;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static com.lulobank.otp.services.v3.domain.error.GeneralErrorStatus.GEN_001;
import static com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus.OTP_180;
import static com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus.OTP_181;
import static com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus.OTP_182;
import static com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus.OTP_184;
import static com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceErrorStatus.OTP_110;
import static com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceErrorStatus.OTP_111;
import static com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus.OTP_115;
import static com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus.OTP_116;


@Getter
@AllArgsConstructor
public enum InboundAdapterErrorCode {
    BAD_REQUEST(List.of(GEN_001.name()), HttpStatus.BAD_REQUEST),

    NOT_FOUND(List.of(OTP_111.name(), OTP_116.name()), HttpStatus.NOT_FOUND),

    PRECONDITION_FAILED(List.of(OTP_184.name()), HttpStatus.PRECONDITION_FAILED),

    INTERNAL_SERVER_ERROR(List.of(OTP_180.name()), HttpStatus.INTERNAL_SERVER_ERROR),

    FORBIDDEN(List.of(OTP_182.name()), HttpStatus.FORBIDDEN),

    NOT_ACCEPTABLE(List.of(OTP_181.name()), HttpStatus.NOT_ACCEPTABLE),

    BAD_GATEWAY(List.of(OTP_110.name(), OTP_115.name()), HttpStatus.BAD_GATEWAY),
    ;

    private List<String> businessCodes;
    private HttpStatus httpStatus;
}
