package com.lulobank.otp.services.actions;

import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.features.onboardingotp.model.OtpRequest;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpMessageToSend;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.utils.OtpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lulobank.otp.services.utils.SMSConstants.OTP_MESSAGE;

@Deprecated
/**
 * @Deprecated since 04/12/2020, Remove this code after deploy PIS-21674
 * otp-business-capability/pull/233,
 * Responsibility for sending SMS is handled by Client-alerts,
 * it is changed to send message to client-alerts queue
 */
public class SendOtpToSNSAction implements Action<Response<OtpResponse>, OtpRequest> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SendOtpToSNSAction.class);
  private SMSMessageSender smsMessageSender;

  public SendOtpToSNSAction(SMSMessageSender smsMessageSender) {
    this.smsMessageSender = smsMessageSender;
  }

  @Override
  public void run(Response<OtpResponse> response, OtpRequest command) {
    String message = OTP_MESSAGE + response.getContent().getOtp();
    smsMessageSender.sendMessage(
      new OtpMessageToSend(OtpUtils.getPhonePrefix(command.getPrefix(), command.getPhoneNumber()), message));
    LOGGER.info("OTP Message sent to: {}", command.getPhoneNumber());
  }
}
