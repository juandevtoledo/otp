package com.lulobank.otp.services.ports.out;

import com.lulobank.otp.sdk.dto.events.NewPushMessageNotification;

public interface PushNotificationService {
    void sendPushNotification(NewPushMessageNotification messageNotification);
}
