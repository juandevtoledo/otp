package com.lulobank.otp.services.v3.domain;

import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OTPArrangement {

    private int length;

    private int expirationTime;

    private TimeUnit timeUnit;

    private List<OTPChannel> targets;

}
