package com.lulobank.otp.services.v3.port.out.redis.error;


import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.util.HttpDomainStatus;

import static com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus.OTP_115;
import static com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus.OTP_116;

public class KeyValRepositoryError extends UseCaseResponseError {
    public KeyValRepositoryError(KeyValRepositoryErrorStatus keyValRepositoryErrorStatus, String providerCode) {
        super(keyValRepositoryErrorStatus.name(), providerCode, KeyValRepositoryErrorStatus.DEFAULT_DETAIL);
    }

    public static KeyValRepositoryError hasKeyNotFound() {
        return new KeyValRepositoryError(OTP_116, String.valueOf(HttpDomainStatus.NOT_FOUND.value()));
    }

    public static KeyValRepositoryError connectionError() {
        return new KeyValRepositoryError(OTP_115, String.valueOf(HttpDomainStatus.BAD_GATEWAY.value()));
    }

}
