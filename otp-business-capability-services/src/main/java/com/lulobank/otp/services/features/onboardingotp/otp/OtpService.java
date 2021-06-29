package com.lulobank.otp.services.features.onboardingotp.otp;

public class OtpService {

    private static OtpService INSTANCE;

    private OtpService() {
    }

    public static OtpService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OtpService();
        }
        return INSTANCE;
    }


    public String create()  {
        return OTPFactory.create();
    }

    public String createOtpCardlessWithdrawal()  {
        return OTPFactory.create(6);
    }

    public String create(int length)  {
        return OTPFactory.create(length);
    }
}
