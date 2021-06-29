package com.lulobank.otp.services.utils;

import com.lulobank.otp.sdk.dto.events.EmailMessageNotification;
import com.lulobank.otp.sdk.dto.events.NewPushMessageNotification;
import com.lulobank.otp.sdk.dto.events.NewSMSMessageNotification;
import com.lulobank.otp.services.features.emailnotification.factories.NewEmailMessageFactory;
import com.lulobank.otp.services.features.onboardingotp.factories.EventHandlerFactory;
import com.lulobank.otp.services.features.onboardingotp.factories.NewSMSMessageFactory;
import com.lulobank.otp.services.features.pushnotification.factories.NewPushNotificationMessageFactory;
import com.lulobank.otp.services.outbounadadapters.services.SESEmailSender;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import com.lulobank.otp.services.ports.service.FileService;
import io.vavr.MatchError;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

public class EventHandlerFactoryTest {
  private EventPayLoadClassFactory testedClass;
  @Mock
  private SMSMessageSender smsMessageSender;
  @Mock
  private SESEmailSender sesEmailSender;
  @Mock
  private PushNotificationService pushNotificationService;
  @Mock
  private FileService fileService;

  private Map<String, Object> headers;

  @Before
  public void init() {
    testedClass = new EventPayLoadClassFactory(smsMessageSender, sesEmailSender,pushNotificationService, StringUtils.EMPTY,
            StringUtils.EMPTY, fileService);
    headers = new HashMap<>();
    headers.put("authenticationToken", "fakeToken");
  }

  @Test(expected = MatchError.class)
  public void handlerNotFound() {
    EventHandlerFactory eventHandler = testedClass.eventHandlerFactory(headers,"");
    assertNull(eventHandler);
  }

  @Test
  public void Handler_NewSMSMessageFactory_Found() {
    EventHandlerFactory eventHandler = testedClass.eventHandlerFactory(headers, NewSMSMessageNotification.class.getSimpleName());
    assertNotNull(eventHandler);
    assertTrue(eventHandler instanceof NewSMSMessageFactory);
  }

  @Test
  public void Handler_NewEmailMessageFactory_Found() {
    EventHandlerFactory eventHandler = testedClass.eventHandlerFactory(headers, EmailMessageNotification.class.getSimpleName());
    assertNotNull(eventHandler);
    assertTrue(eventHandler instanceof NewEmailMessageFactory);
  }
  @Test
  public void Handler_NewPushNotificationMessageFactory_Found() {
    EventHandlerFactory eventHandler = testedClass.eventHandlerFactory(headers, NewPushMessageNotification.class.getSimpleName());
    assertNotNull(eventHandler);
    assertTrue(eventHandler instanceof NewPushNotificationMessageFactory);
  }
}
