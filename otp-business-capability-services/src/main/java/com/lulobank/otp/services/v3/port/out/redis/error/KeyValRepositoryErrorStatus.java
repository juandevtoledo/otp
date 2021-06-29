package com.lulobank.otp.services.v3.port.out.redis.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @apiNote This enum must handle business codes from OTP_115 to OTP_119
 */
@Getter
@AllArgsConstructor
public enum KeyValRepositoryErrorStatus {
    OTP_115("Connection error on redis service"),
    OTP_116("Hash key not found"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "D";
}
