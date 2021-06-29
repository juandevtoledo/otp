package com.lulobank.otp.sdk.dto.exceptions;

public class AuthorizationIvrException extends RuntimeException{


    private static final long serialVersionUID = 1260077061912246754L;
    private final int code;

    public AuthorizationIvrException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }


}
