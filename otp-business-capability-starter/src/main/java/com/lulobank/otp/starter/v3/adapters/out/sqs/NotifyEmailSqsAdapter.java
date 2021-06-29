package com.lulobank.otp.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;
import com.lulobank.otp.services.ports.out.messaging.MessagingPort;
import com.lulobank.otp.services.ports.out.messaging.dto.EmailNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NotifyEmailSqsAdapter implements MessagingPort<EmailNotificationMessage> {

    private final String clientAlertsQueueEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public void sendMessage(EmailNotificationMessage emailNotificationMessage) {
        Event<EmailNotificationMessage> emailNotificationMessageEvent = EventFactory
                .ofDefaults(emailNotificationMessage)
                .build();
        sqsBraveTemplate.convertAndSend(clientAlertsQueueEndpoint, emailNotificationMessageEvent);
        log.info("Create otp message Email for email {} has been sent to queue {}", emailNotificationMessage.getTo(),
                clientAlertsQueueEndpoint);
    }
}
