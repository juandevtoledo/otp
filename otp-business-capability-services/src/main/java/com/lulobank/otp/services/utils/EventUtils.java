package com.lulobank.otp.services.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.core.events.Event;
import com.lulobank.otp.services.exceptions.OtpException;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class EventUtils<T> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventUtils.class);

  //TO DO: Quitar esta clase, cuando este codigo este en el core
  public static Event getEvent(String json, Class<?> eventTypeClass) {
      return (Event) Try.of(() -> {
          ObjectMapper mapper = new ObjectMapper();
          return mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                  .readValue(json, mapper.getTypeFactory()
                          .constructParametricType(Event.class, eventTypeClass));
      })
              .onFailure(IOException.class, e -> LOGGER.error("Event has error: ", e)).get();
  }

  public static String getEventType(String json) throws OtpException {
      return Try.of(() -> new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
              .readValue(json, Event.class).getEventType())
              .getOrElseThrow(e -> new OtpException(e, 500, e.getMessage()));
  }

  public Event getEvent(T t) {
    Event<T> event = new Event<>();
    event.setEventType(t.getClass().getSimpleName());
    event.setPayload(t);
    event.setId(UUID.randomUUID().toString());
    return event;
  }
}
