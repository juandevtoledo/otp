package com.lulobank.otp.services.exceptions;

public class NotValidDistributionChannelSupportedException extends Exception {

    public NotValidDistributionChannelSupportedException(String channel) {
        super(String.format("The channel  %s is not supported ", channel));
    }

}