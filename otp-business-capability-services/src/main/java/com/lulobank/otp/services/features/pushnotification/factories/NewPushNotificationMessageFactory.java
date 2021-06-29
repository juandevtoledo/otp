package com.lulobank.otp.services.features.pushnotification.factories;

import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.otp.sdk.dto.events.NewPushMessageNotification;
import com.lulobank.otp.services.features.onboardingotp.factories.EventHandlerFactory;
import com.lulobank.otp.services.features.pushnotification.NewPushNotificationEventHandler;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import com.lulobank.otp.services.utils.EventUtils;

public class NewPushNotificationMessageFactory implements EventHandlerFactory {
    private PushNotificationService pushNotificationService;

    public NewPushNotificationMessageFactory(PushNotificationService pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public EventHandler createEventHandler() {
        return new NewPushNotificationEventHandler(pushNotificationService);
    }

    @Override
    public Event createEvent(String jsonEvent) {
        return EventUtils.getEvent(jsonEvent, NewPushMessageNotification.class);
    }
}
