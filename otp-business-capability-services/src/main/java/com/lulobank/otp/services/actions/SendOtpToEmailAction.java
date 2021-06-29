package com.lulobank.otp.services.actions;

import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.features.onboardingotp.model.OtpEmailRequest;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Deprecated
  /**
   * @Deprecated since 04/12/2020, Remove this code after deploy PIS-21674
   * otp-business-capability/pull/233,
   * Responsibility for sending email is handled by Client-alerts,
   * it is changed to send message to client-alerts queue
   */
public class SendOtpToEmailAction implements Action<Response<OtpResponse>, OtpEmailRequest> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SendOtpToEmailAction.class);
  private MailSender mailSender;
  private String emailSenderAddress;
  private String emailBcc;
  private String emailSubject;
  private String emailMessage;


  public SendOtpToEmailAction(MailSender mailSender, String emailSenderAddress, String emailBcc, String emailSubject, String emailMessage) {
    this.mailSender = mailSender;
    this.emailSenderAddress = emailSenderAddress;
    this.emailBcc = emailBcc;
    this.emailSubject = emailSubject;
    this.emailMessage = emailMessage;
  }


  @Override
  public void run(Response<OtpResponse> otpResponseResponse, OtpEmailRequest otpEmailRequest) {
    otpEmailRequest.getEmailTemplate().setBody(emailMessage + otpResponseResponse.getContent().getOtp());
    otpEmailRequest.getEmailTemplate().setSubject(emailSubject);
    otpEmailRequest.getEmailTemplate().setFrom(emailSenderAddress);
    otpEmailRequest.getEmailTemplate().setBcc(Arrays.asList(emailBcc));
    mailSender.sendEmail(otpEmailRequest.getEmailTemplate());
    LOGGER.info("OTP Message sent to: {}", otpEmailRequest.getEmailTemplate().getTo());
  }
}
