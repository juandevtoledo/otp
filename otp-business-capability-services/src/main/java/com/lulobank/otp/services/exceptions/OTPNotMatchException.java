package com.lulobank.otp.services.exceptions;

public class OTPNotMatchException extends Exception {

    public OTPNotMatchException(String otp) {
        super(String.format("The otp %s does not match", otp));
    }

}
