package com.lulobank.otp.sdk.dto.exceptions;

import com.lulobank.utils.exception.ServiceException;

public class AuthorizationSacException extends ServiceException {


    public AuthorizationSacException(String message) {
        super(message);
    }

    public AuthorizationSacException(int code, String message) {
        super(code, message);
    }

    public AuthorizationSacException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
