package com.lulobank.otp.starter.v3.adapters.out.redis;

import com.lulobank.otp.services.v3.port.out.redis.HashRepositoryPort;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus;
import com.lulobank.otp.starter.v3.adapters.out.redis.model.OtpRedisEntity;
import com.lulobank.otp.starter.v3.adapters.out.redis.repository.HashRedisRepository;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class HashRepositoryAdapter implements HashRepositoryPort {
    private final HashRedisRepository hashRedisRepository;


    @Override
    public Either<KeyValRepositoryError, String> findOTPById(String id) {
        return Try.of(() -> Option.ofOptional(hashRedisRepository.findById(id)))
                .fold(throwable -> Either.left(handleException(throwable, id)), this::handleNotFound);
    }

    @Override
    public Either<KeyValRepositoryError, Boolean> deleteOTPById(String id) {
        return Try.run(() -> hashRedisRepository.deleteById(id))
                .fold(throwable -> Either.left(handleException(throwable, id)), unused -> Either.right(true));
    }

    private KeyValRepositoryError handleException(Throwable t, String key) {
        log.error("Error getting redis registry for key {}", key, t);
        return KeyValRepositoryError.connectionError();
    }

    private Either<KeyValRepositoryError, String> handleNotFound(Option<OtpRedisEntity> otpOp) {
        log.error(KeyValRepositoryErrorStatus.OTP_116.getMessage());
        return otpOp
                .map(OtpRedisEntity::getOtp)
                .fold(() -> Either.left(KeyValRepositoryError.hasKeyNotFound()), Either::right);
    }
}
