package com.lulobank.otp.services.exceptions;

public class NotValidTransactionDataException extends Exception {
    public NotValidTransactionDataException(Class expectedType, String payload, Throwable cause){
        super(String.format("The payload  %s does not conform to a %s",payload,expectedType.getCanonicalName()),cause);
    }
}