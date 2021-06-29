package com.lulobank.otp.services.utils;

import com.lulobank.otp.sdk.dto.events.EmailMessageNotification;
import com.lulobank.otp.sdk.dto.events.NewPushMessageNotification;
import com.lulobank.otp.sdk.dto.events.NewSMSMessageNotification;
import com.lulobank.otp.services.features.emailnotification.factories.NewEmailMessageFactory;
import com.lulobank.otp.services.features.onboardingotp.factories.EventHandlerFactory;
import com.lulobank.otp.services.features.onboardingotp.factories.NewSMSMessageFactory;
import com.lulobank.otp.services.features.pushnotification.factories.NewPushNotificationMessageFactory;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import com.lulobank.otp.services.ports.service.FileService;
import io.vavr.API;

import java.util.Map;

import static io.vavr.API.$;
import static io.vavr.API.Case;

public class EventPayLoadClassFactory {
  private SMSMessageSender smsMessageSender;
  private MailSender mailSender;
  private PushNotificationService pushNotificationService;
  private String emailSenderAddress;
  private String emailBcc;
  private FileService fileService;


  public EventPayLoadClassFactory(SMSMessageSender smsMessageSender, MailSender mailSender,
                                  PushNotificationService pushNotificationService, String emailSenderAddress,
                                  String emailBcc,FileService fileService) {
    this.smsMessageSender = smsMessageSender;
    this.mailSender = mailSender;
    this.pushNotificationService = pushNotificationService;
    this.emailSenderAddress = emailSenderAddress;
    this.emailBcc = emailBcc;
    this.fileService = fileService;
  }

  public EventHandlerFactory eventHandlerFactory(Map<String, Object> headers, String eventPayloadClass) {

    return API.Match(eventPayloadClass).of(
            Case($(NewSMSMessageNotification.class.getSimpleName()), () -> new NewSMSMessageFactory(smsMessageSender)),
            Case($(EmailMessageNotification.class.getSimpleName()), () -> new NewEmailMessageFactory(mailSender, emailSenderAddress, emailBcc, fileService)),
            Case($(NewPushMessageNotification.class.getSimpleName()), () -> new NewPushNotificationMessageFactory(pushNotificationService))
    );
  }
}
