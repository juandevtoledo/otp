package com.lulobank.otp.services.v3.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OTPGenerationRs {

    private OTPChannel channel;

    private int length;

    private int expirationTimeInSec;

    private String hash;
}