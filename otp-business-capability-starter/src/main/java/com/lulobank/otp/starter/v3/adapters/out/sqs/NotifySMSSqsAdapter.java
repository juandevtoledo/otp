package com.lulobank.otp.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;
import com.lulobank.otp.services.ports.out.messaging.MessagingPort;
import com.lulobank.otp.services.ports.out.messaging.dto.SMSNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class NotifySMSSqsAdapter implements MessagingPort<SMSNotificationMessage> {

    private final String clientAlertsQueueEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public void sendMessage(SMSNotificationMessage smsNotificationMessage) {
        Event<SMSNotificationMessage> smsNotificationMessageEvent = EventFactory
                .ofDefaults(smsNotificationMessage)
                .build();
        sqsBraveTemplate.convertAndSend(clientAlertsQueueEndpoint, smsNotificationMessageEvent);
        log.info("Create otp message SMS for phone {} has been sent to queue {}", smsNotificationMessage.getPhoneNumber(),
                clientAlertsQueueEndpoint);
    }
}
