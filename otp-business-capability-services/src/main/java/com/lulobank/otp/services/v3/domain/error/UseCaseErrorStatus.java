package com.lulobank.otp.services.v3.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from TRA_180 to TRA_199
 */
@Getter
@RequiredArgsConstructor
public enum UseCaseErrorStatus {
    OTP_180("Error default"),
    OTP_181("Invalid transaction type sac"),
    OTP_182("Invalid otp"),
    OTP_183("Error generating the operation hash"),
    OTP_184("Otp does not match")
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "U";
}
