package com.lulobank.otp.sdk.operations.exceptions;

public class VerifyHireCreditException extends RuntimeException {

    private int code;

    public VerifyHireCreditException(int code, String message) {
        super(message);
        this.code = code;
    }

    public VerifyHireCreditException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}
