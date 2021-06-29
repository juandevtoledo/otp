package com.lulobank.otp.services.v3.usecase;

import com.lulobank.otp.services.exceptions.OTPNotFoundException;
import com.lulobank.otp.services.exceptions.OTPNotMatchException;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import com.lulobank.otp.services.v3.domain.VerifyOTPRs;
import com.lulobank.otp.services.v3.port.in.VerifyOTP;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.services.v3.services.KeyService;
import io.vavr.Tuple;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class VerifyOTPUseCase implements VerifyOTP {

    private KeyService keyService;

    private KeyValRepository keyValRepository;

    public VerifyOTPUseCase(KeyService keyService, KeyValRepository keyValRepository) {
        this.keyService = keyService;
        this.keyValRepository = keyValRepository;
    }

    @Override
    public Try<VerifyOTPRs> execute(OTPValidationRq command) {
        return keyService.calculate(command)
                .map(hash -> Tuple.of(hash, keyValRepository.get(hash)))
                .flatMap(tuple -> validateAndRemoveOTP(tuple._1, tuple._2, command.getOtp()));
    }

    private Try<VerifyOTPRs> validateAndRemoveOTP(String hash, Option<String> redisOTP, String headerOtp) {
        return redisOTP
                .toTry(() -> new OTPNotFoundException(headerOtp))
                .filter(ro -> Objects.equals(ro, headerOtp), ro -> new OTPNotMatchException(headerOtp))
                .flatMap(re -> keyValRepository.remove(hash))
                .flatMap(resp -> Try.success(new VerifyOTPRs(true)));
    }

}
