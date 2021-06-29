package com.lulobank.otp.services.v3.services;

import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import io.vavr.control.Try;

public interface KeyService {

    Try<String> calculate(OTPValidationRq command);

}
