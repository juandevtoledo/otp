package com.lulobank.otp.services.features.pushnotification;

import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.otp.sdk.dto.events.NewPushMessageNotification;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewPushNotificationEventHandler implements EventHandler<Event<NewPushMessageNotification>> {

    private PushNotificationService pushNotificationService;

    public NewPushNotificationEventHandler(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public void apply(Event<NewPushMessageNotification> event) {
        Try.run(() -> pushNotificationService.sendPushNotification(event.getPayload()))
                .onFailure(Exception.class, e -> log.error("Error sending push notification, Error {}",e.getMessage(),e));
    }
}