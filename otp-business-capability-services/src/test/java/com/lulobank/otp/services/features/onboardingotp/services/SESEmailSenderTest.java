package com.lulobank.otp.services.features.onboardingotp.services;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.lulobank.core.helpers.GlobalConstants;
import com.lulobank.otp.services.outbounadadapters.services.SESEmailSender;
import com.lulobank.otp.sdk.dto.email.EmailAttachment;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SESEmailSenderTest {
  private static final String EMAIL_FROM = "mail@mail.com";
  private static final String EMAIL_TO = "mail@mail.com";
  private static final String EMAIL_BCC = "mail@mail.com";
  private SESEmailSender testedClass;
  private EmailTemplate emailTemplate;
  @Mock
  private AmazonSimpleEmailService amazonSimpleEmailService;
  @Captor
  private ArgumentCaptor<SendEmailRequest> sendPlainEmailRequestArgumentCaptor;
  @Captor
  private ArgumentCaptor<SendRawEmailRequest> sendRawEmailRequestArgumentCaptor;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    testedClass = new SESEmailSender(amazonSimpleEmailService);
    emailTemplate = new EmailTemplate();
    emailTemplate.setFrom(EMAIL_FROM);
    emailTemplate.getTo().add(EMAIL_TO);
    emailTemplate.getBcc().add(EMAIL_BCC);
    emailTemplate.setBody(GlobalConstants.EMPTY_STRING);
    emailTemplate.setSubject(GlobalConstants.EMPTY_STRING);
  }

  @Test
  public void Plain_Email_Sent() {
    testedClass.sendEmail(emailTemplate);
    verify(amazonSimpleEmailService, times(1)).sendEmail(sendPlainEmailRequestArgumentCaptor.capture());
    assertTrue(sendPlainEmailRequestArgumentCaptor.getValue().getDestination().getToAddresses().get(0).equals(EMAIL_TO));
    assertTrue(sendPlainEmailRequestArgumentCaptor.getValue().getDestination().getBccAddresses().get(0).equals(EMAIL_BCC));
    assertTrue(sendPlainEmailRequestArgumentCaptor.getValue().getMessage().getBody().getText().getData()
                 .equals(GlobalConstants.EMPTY_STRING));
    assertTrue(sendPlainEmailRequestArgumentCaptor.getValue().getMessage().getSubject().getData()
                 .equals(GlobalConstants.EMPTY_STRING));
  }

  @Test
  public void Raw_Email_Sent() {
    emailTemplate.getFiles().add(new EmailAttachment("", new byte[0], "application/pdf"));
    testedClass.sendEmail(emailTemplate);
    verify(amazonSimpleEmailService, times(1)).sendRawEmail(sendRawEmailRequestArgumentCaptor.capture());
    assertNotNull(sendRawEmailRequestArgumentCaptor.getValue().getRawMessage().getData());
  }
}
