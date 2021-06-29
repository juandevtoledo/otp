package com.lulobank.otp.services.v3.port.out.redis;

import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import io.vavr.control.Either;

public interface HashRepositoryPort {
    Either<KeyValRepositoryError, String> findOTPById(String id);

    Either<KeyValRepositoryError, Boolean> deleteOTPById(String id);
}
