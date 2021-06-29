package com.lulobank.otp.services.ports.out.messaging.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class SMSNotificationMessage {
    private String clientId;
    private String notificationType;
    private String phonePrefix;
    private String phoneNumber;
    private Map<String, Object> attributes;
}
