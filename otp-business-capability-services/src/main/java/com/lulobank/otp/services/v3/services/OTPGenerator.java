package com.lulobank.otp.services.v3.services;

import com.lulobank.otp.services.features.onboardingotp.otp.OTPFactory;
import com.lulobank.otp.services.v3.domain.OTPArrangement;
import io.vavr.control.Try;

public class OTPGenerator {

    public Try<String> create(OTPArrangement arrangement) {
        return Try.of(() -> OTPFactory.create(arrangement.getLength()));
    }

}
