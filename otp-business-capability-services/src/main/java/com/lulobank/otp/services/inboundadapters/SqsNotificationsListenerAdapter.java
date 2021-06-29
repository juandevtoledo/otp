package com.lulobank.otp.services.inboundadapters;

import com.lulobank.otp.services.exceptions.OtpException;
import com.lulobank.otp.services.features.onboardingotp.EventsNotificator;
import com.lulobank.otp.services.features.onboardingotp.factories.EventHandlerFactory;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import com.lulobank.otp.services.ports.service.FileService;
import com.lulobank.otp.services.utils.EventPayLoadClassFactory;
import com.lulobank.otp.services.utils.EventUtils;
import io.vavr.MatchError;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@Slf4j
public class SqsNotificationsListenerAdapter {
    private SMSMessageSender smsMessageSender;
    private MailSender mailSender;
    private PushNotificationService pushNotificationService;
    private FileService fileService;
    @Value("${mail.sender}")
    private String emailSenderAddress;
    @Value("${mail.bcc}")
    private String emailBcc;

    @Autowired

    public SqsNotificationsListenerAdapter(SMSMessageSender smsMessageSender, MailSender mailSender,
                                           PushNotificationService pushNotificationService,
                                           FileService fileService) {
        this.smsMessageSender = smsMessageSender;
        this.mailSender = mailSender;
        this.pushNotificationService = pushNotificationService;
        this.fileService = fileService;
    }

    @SqsListener(value = "${cloud.aws.end-point.uri-notifications}", deletionPolicy = SqsMessageDeletionPolicy.ALWAYS)
    public void getMessage(@Headers final Map<String, Object> headers, @Payload final String eventJsonString) {

        Try.run(() -> {
            log.info("Event read from notifications queue: {}", eventJsonString);
            String eventType = EventUtils.getEventType(eventJsonString);
            EventHandlerFactory eventHandlerFactory = new EventPayLoadClassFactory(smsMessageSender, mailSender,
                    pushNotificationService, emailSenderAddress, emailBcc, fileService).eventHandlerFactory(headers, eventType);
            new EventsNotificator(eventHandlerFactory, eventJsonString).notifyEvent();
        })
                .onFailure(OtpException.class, e -> log.error("Error handling sqs message, Error {} {}", e.getMessage(), e))
                .onFailure(MatchError.class, e -> log.error("Unable to process sqs message, Error {}.",e.getMessage()));

    }
}
