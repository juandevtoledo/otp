package com.lulobank.otp.services.features.onboardingotp.services;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.PublishRequest;
import com.lulobank.otp.services.outbounadadapters.services.NewSMSMessageToSend;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageAsyncHandler;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageAttribute;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.utils.OtpUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SMSMessageSenderTest {
  private SMSMessageSender testedClass;
  @Mock
  private AmazonSNSAsync amazonSNSAsync;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    testedClass = new SMSMessageSender(amazonSNSAsync, new SMSMessageAttribute("Test", "0", "Transactions"));
  }

  @Test
  public void messageSent() {
    testedClass.sendMessage(new NewSMSMessageToSend(OtpUtils.getPhonePrefix("57", "3000000001"), "Test Message"));
    verify(amazonSNSAsync, times(1)).publishAsync(any(PublishRequest.class), any(SMSMessageAsyncHandler.class));
  }
}
