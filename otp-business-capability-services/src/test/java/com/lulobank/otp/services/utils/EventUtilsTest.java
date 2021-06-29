package com.lulobank.otp.services.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.core.events.Event;
import com.lulobank.otp.sdk.dto.events.NewSMSMessageNotification;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EventUtilsTest {
  @Test
  public void suscessEventCreated() throws JsonProcessingException {
    NewSMSMessageNotification message = new NewSMSMessageNotification();
    message.setMessage("Test Message");
    message.setPhoneNumber("3228591725");
    message.setPhonePrefix("57");
    Event<NewSMSMessageNotification> event = new EventUtils().getEvent(message);
    ObjectMapper objectMapper = new ObjectMapper();
    Event<NewSMSMessageNotification> eventResult = EventUtils.getEvent(objectMapper.writeValueAsString(event),
      NewSMSMessageNotification.class);
    assertNotNull(eventResult);
    assertEquals(message.getMessage(), eventResult.getPayload().getMessage());
    assertEquals(message.getPhonePrefix(), eventResult.getPayload().getPhonePrefix());
    assertEquals(message.getPhoneNumber(), eventResult.getPayload().getPhoneNumber());
  }
}
