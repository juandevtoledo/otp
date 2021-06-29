package com.lulobank.otp.services.v3.port.in;

import com.lulobank.otp.services.v3.UseCase;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;

public interface GenerateOTP extends UseCase<OTPValidationRq, String> {
}
