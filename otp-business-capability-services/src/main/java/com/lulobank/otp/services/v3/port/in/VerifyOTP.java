package com.lulobank.otp.services.v3.port.in;

import com.lulobank.otp.services.v3.UseCase;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import com.lulobank.otp.services.v3.domain.VerifyOTPRs;

public interface VerifyOTP extends UseCase<OTPValidationRq, VerifyOTPRs> {
}
