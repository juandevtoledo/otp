package com.lulobank.otp.services.v3.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public final class OperationUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private OperationUtils() {
    }

    public static Either<UseCaseResponseError, String> getOperationHash(Object operation) {
        return Try.of(() -> objectMapper.writeValueAsString(operation).getBytes(StandardCharsets.UTF_8))
                .map(bytes -> Base64.getEncoder().encodeToString(bytes))
                .fold(throwable -> Either.left(handleHashGeneratorError(throwable)), Either::right);
    }

    private static UseCaseResponseError handleHashGeneratorError(Throwable t) {
        log.error(UseCaseErrorStatus.OTP_183.getMessage(), t);
        return new UseCaseResponseError(UseCaseErrorStatus.OTP_183.name(), String.valueOf(HttpDomainStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
