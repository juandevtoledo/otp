package com.lulobank.otp.services.features.onboardingotp;

import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.otp.services.features.onboardingotp.factories.EventHandlerFactory;

public class EventsNotificator {
  private Event event;
  private EventHandler eventHandler;

  public EventsNotificator(EventHandlerFactory factory, String jsonEvent) {
    event = factory.createEvent(jsonEvent);
    eventHandler = factory.createEventHandler();
  }

  public void notifyEvent() {
    eventHandler.apply(event);
  }
}
