package com.lulobank.otp.starter.v3.adapters.out.redis;

import com.lulobank.otp.services.v3.domain.OTPArrangement;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus.OTP_115;

@Slf4j
public class KeyValRepositoryImpl implements KeyValRepository {

    private static final int OTP_EMAIL_EXPIRATION_TIME = 180;//180

    private ValueOperations<String, String> valueOperations;

    @Autowired
    public KeyValRepositoryImpl(StringRedisTemplate stringRedisTemplate) {
        this.valueOperations = stringRedisTemplate.opsForValue();
    }

    @Override
    public Try<String> save(String hash, String otp, OTPTransaction otpTransaction) {
        OTPArrangement config = otpTransaction.getArrangement();

        return Try.of(() -> {
            valueOperations.set(hash, otp, config.getExpirationTime(), config.getTimeUnit());
            return otp;
        });
    }

    @Override
    public Option<String> get(String key) {
        log.info("look up for key {}", key);
        return Option.of(valueOperations.get(key));
    }

    @Override
    public Try<Boolean> remove(String key) {
        return Try.of(() ->valueOperations.getOperations().delete(key));
    }

    @Override
    public Either<KeyValRepositoryError, String> save(String key, String otp) {
        return Try.of(() -> {
                        valueOperations.set(key, otp, OTP_EMAIL_EXPIRATION_TIME, TimeUnit.SECONDS);
                        return otp;
                    })
                .fold(this::handleException, Either::right);
    }

    @Override
    public Either<KeyValRepositoryError, Option<String>> getByKey(String key) {
        return Try.of(() -> Option.of(valueOperations.get(key)))
                .fold(this::handleException, Either::right);
    }

    private <T> Either<KeyValRepositoryError, T> handleException(Throwable t) {
        log.error(OTP_115.getMessage(), t);
        return Either.left(KeyValRepositoryError.connectionError());
    }
}
