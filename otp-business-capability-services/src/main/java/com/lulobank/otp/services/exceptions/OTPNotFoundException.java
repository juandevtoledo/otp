package com.lulobank.otp.services.exceptions;

public class OTPNotFoundException extends Exception {

    public OTPNotFoundException(String hash) {
        super(String.format("Requested otp %s is not present", hash));
    }

}
