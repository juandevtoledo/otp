package com.lulobank.otp.services.v3.services;

import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import io.vavr.control.Try;

public class DefaultKeyService implements KeyService {


    @Override
    public Try<String> calculate(OTPValidationRq command) {
        return command.getTransactionType()
                .toTransactionData(command.getPayload())
                .flatMapTry(data -> data.calculateOtpKey(command.getClientId()));
    }

}
