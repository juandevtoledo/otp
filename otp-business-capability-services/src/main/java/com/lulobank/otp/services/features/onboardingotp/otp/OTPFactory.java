package com.lulobank.otp.services.features.onboardingotp.otp;

import io.vavr.concurrent.Future;

import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class OTPFactory {

    public static final int DEFAULT_LIMIT_CONCURRENT = 1000;

    private OTPFactory() {
    }

    private static final SecureRandom rn = new SecureRandom();

    private static final Set<String> pool = ConcurrentHashMap.newKeySet();

    private static Stream<String> listOf(int length) {
        return length >= 0 ?
                Stream.iterate(rn.nextInt(10), n -> rn.nextInt(10)).map(Object::toString)
                        .limit(length) : Stream.of();
    }

    public static String create() {
        return create(null);
    }

    public static String create(Integer length) {
        int otpLength = length == null ? 4 : length;
        String otp = otpOf(otpLength);
        if (pool.contains(otp))
            return create(otpLength);
        pool.add(otp);
        Future.run(() -> {
            if (pool.size() > DEFAULT_LIMIT_CONCURRENT)
                pool.clear();
        });
        return otp;
    }

    private static String otpOf(int otpLength) {
        return listOf(otpLength).reduce("", String::concat);
    }

}
