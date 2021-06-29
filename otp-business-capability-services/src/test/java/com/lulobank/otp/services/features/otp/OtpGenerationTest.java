package com.lulobank.otp.services.features.otp;

import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class OtpGenerationTest {
  private static final int OTP_DEFAULT_LENGTH = 4;
  private static final int OTP_CUSTOM_LENGTH = 6;
  private OtpService testedClass;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    this.testedClass = OtpService.getInstance();
  }

  @Test
  public void validateDefaultOtpLength() {
      Assert.assertEquals(OTP_DEFAULT_LENGTH, testedClass.create().length());
  }

  @Test
  public void validateCustomOtpLength() {
      Assert.assertEquals(OTP_CUSTOM_LENGTH, testedClass.create(6).length());
  }
}
