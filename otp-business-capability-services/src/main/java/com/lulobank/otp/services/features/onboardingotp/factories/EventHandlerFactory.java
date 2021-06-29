package com.lulobank.otp.services.features.onboardingotp.factories;

import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;

public interface EventHandlerFactory {
  EventHandler createEventHandler();

  Event createEvent(String jsonEvent);
}
