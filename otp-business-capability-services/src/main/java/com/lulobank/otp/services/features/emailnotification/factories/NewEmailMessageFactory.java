package com.lulobank.otp.services.features.emailnotification.factories;

import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.otp.sdk.dto.events.EmailMessageNotification;
import com.lulobank.otp.services.features.emailnotification.NewEmailMessageHandler;
import com.lulobank.otp.services.features.onboardingotp.factories.EventHandlerFactory;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import com.lulobank.otp.services.ports.service.FileService;
import com.lulobank.otp.services.utils.EventUtils;

public class NewEmailMessageFactory implements EventHandlerFactory {
  private MailSender mailSender;
  private String emailSenderAddress;
  private String emailBcc;
  private FileService fileService;


  public NewEmailMessageFactory(MailSender mailSender, String emailSenderAddress,
                                String emailBcc, FileService fileService) {
    this.mailSender = mailSender;
    this.emailSenderAddress = emailSenderAddress;
    this.emailBcc = emailBcc;
    this.fileService = fileService;
  }

  @Override
  public EventHandler createEventHandler() {
    return new NewEmailMessageHandler(mailSender, emailSenderAddress, emailBcc, fileService);
  }

  @Override
  public Event createEvent(String jsonEvent) {
    return EventUtils.getEvent(jsonEvent, EmailMessageNotification.class);
  }
}
