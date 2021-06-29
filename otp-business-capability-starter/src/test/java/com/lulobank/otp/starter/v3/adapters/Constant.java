package com.lulobank.otp.starter.v3.adapters;

import java.math.BigInteger;
import java.util.UUID;

public class Constant {
    public static final String ID_CLIENT = UUID.randomUUID().toString();
    public static final String MAIL = "mail@mail.com";
    public static final String PHONE_NUMBER = "3101234567";
    public static final int PHONE_PREFIX = 57;
    public static final String ID_CARD = "1020301010";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String HASH_KEY = "someHash";
    public static final String OTP = "1234";
    public static final BigInteger WITHDRAWAL_AMOUNT = BigInteger.valueOf(10000);
}
