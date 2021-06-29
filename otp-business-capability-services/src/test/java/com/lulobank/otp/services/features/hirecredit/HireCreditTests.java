package com.lulobank.otp.services.features.hirecredit;

import com.lulobank.otp.services.features.otp.GenerateOtpForNewLoanCommand;
import com.lulobank.otp.services.features.otp.ValidateOtpForNewLoanCommand;
import com.lulobank.otp.services.utils.OtpRedisEntityUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HireCreditTests {

    private GenerateOtpForNewLoanCommand otpInput;
    private ValidateOtpForNewLoanCommand otpOutput;

    @Before
    public void init() {
        otpInput = new GenerateOtpForNewLoanCommand("123456", "ASDFGH", "098765");
        otpOutput = new ValidateOtpForNewLoanCommand("123456", "ASDFGH", "098765", "1111");
    }

    @Test
    public void When_generate_id_using_abstractoperation_and_varifyabstractoperation_then_id_should_be_the_same() {

        String keyInput = OtpRedisEntityUtils.generateOtpIdForNewLoan(otpInput.getIdClient(), otpInput.getIdCredit(), otpInput.getIdOffer());
        String keyOutput = OtpRedisEntityUtils.generateOtpIdForNewLoan(otpOutput.getIdClient(), otpOutput.getIdCredit(), otpOutput.getIdOffer());

        Assert.assertEquals(keyInput, keyOutput);
    }


}
