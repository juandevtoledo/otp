package com.lulobank.otp.sdk.dto.exceptions;

import com.lulobank.utils.exception.ServiceException;

public class OtpValidationException extends ServiceException {
    public OtpValidationException(String message) {
        super(message);
    }

    public OtpValidationException(int code, String message) {
        super(code, message);
    }

    public OtpValidationException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
