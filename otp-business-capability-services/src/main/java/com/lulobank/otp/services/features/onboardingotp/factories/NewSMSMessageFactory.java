package com.lulobank.otp.services.features.onboardingotp.factories;

import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.otp.sdk.dto.events.NewSMSMessageNotification;
import com.lulobank.otp.services.features.onboardingotp.NewSMSMessageHandler;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.utils.EventUtils;

public class NewSMSMessageFactory implements EventHandlerFactory {
  private SMSMessageSender smsMessageSender;

  public NewSMSMessageFactory(SMSMessageSender smsMessageSender) {
    this.smsMessageSender = smsMessageSender;
  }

  @Override
  public EventHandler createEventHandler() {
    return new NewSMSMessageHandler(smsMessageSender);
  }

  @Override
  public Event createEvent(String jsonEvent) {
    return EventUtils.getEvent(jsonEvent, NewSMSMessageNotification.class);
  }
}
