package com.lulobank.otp.services.features.onboardingotp;

import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.otp.sdk.dto.events.NewSMSMessageNotification;
import com.lulobank.otp.services.outbounadadapters.services.NewSMSMessageToSend;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.utils.LogMessages;
import com.lulobank.otp.services.utils.OtpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewSMSMessageHandler implements EventHandler<Event<NewSMSMessageNotification>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NewSMSMessageHandler.class);
  private SMSMessageSender smsMessageSender;

  public NewSMSMessageHandler(SMSMessageSender smsMessageSender) {
    this.smsMessageSender = smsMessageSender;
  }

    @Override
    public void apply(Event<NewSMSMessageNotification> event) {
        NewSMSMessageNotification newSMSMessageNotification = event.getPayload();
        LOGGER.info(LogMessages.SENDING_SMS_MESSAGE.getMessage(), newSMSMessageNotification.getPhoneNumber());
        NewSMSMessageToSend newSMSMessageToSend = new NewSMSMessageToSend(OtpUtils.getPhonePrefix(newSMSMessageNotification.getPhonePrefix(),
                newSMSMessageNotification.getPhoneNumber()), newSMSMessageNotification.getMessage());
        smsMessageSender.sendMessage(newSMSMessageToSend);

    }
}
