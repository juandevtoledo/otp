package com.lulobank.otp.services.v3.port.out.redis;

import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;

public interface KeyValRepository {

    Try<String> save(String hash, String otp, OTPTransaction transactionType);

    Option<String> get(String key);

    Try<Boolean> remove(String key);

    Either<KeyValRepositoryError, String> save(String key, String otp);

    Either<KeyValRepositoryError, Option<String>> getByKey(String key);

}
