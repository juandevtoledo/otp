package com.lulobank.otp.services.v3.port.out.clients.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @apiNote This enum must handle business codes from OTP_110 to OTP_114
 */
@Getter
@AllArgsConstructor
public enum ClientsServiceErrorStatus {
    OTP_110("Connection error on clients service"),
    OTP_111("Client not found"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "S";
}
