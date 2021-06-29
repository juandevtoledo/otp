package com.lulobank.otp.services.v3.usecase;

import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import com.lulobank.otp.services.v3.port.in.GenerateOTP;
import com.lulobank.otp.services.v3.port.out.notifactions.NotifyService;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.services.v3.services.KeyService;
import com.lulobank.otp.services.v3.services.OTPGenerator;
import io.vavr.control.Try;

public class GenerateOTPUseCase implements GenerateOTP {

    private KeyService keyService;

    private KeyValRepository keyValRepository;

    private NotifyService notifyService;

    private OTPGenerator otpGenerator;

    public GenerateOTPUseCase(KeyService keyService, KeyValRepository keyValRepository,
                              NotifyService notifyService, OTPGenerator otpGenerator) {
        this.keyService = keyService;
        this.keyValRepository = keyValRepository;
        this.notifyService = notifyService;
        this.otpGenerator = otpGenerator;
    }

    @Override
    public Try<String> execute(OTPValidationRq command) {
        return keyService.calculate(command)
                .flatMap(hash -> generateOtp(hash, command.getTransactionType()))
                .peek(otp -> notifyService.notify(command.getAuthorizationHeader(), otp, command.getClientId(),
                        command.getTransactionType(),command.getPayload()));
    }

    private Try<String> generateOtp(String hash, OTPTransaction transaction) {
        return otpGenerator.create(transaction.getArrangement())
                .flatMap(otp -> keyValRepository.save(hash, otp, transaction));
    }
}
