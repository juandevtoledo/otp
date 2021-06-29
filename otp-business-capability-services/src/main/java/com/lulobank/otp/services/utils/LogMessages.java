package com.lulobank.otp.services.utils;

public enum LogMessages {
    SENDING_MESSAGE_TO("Sending email message to: {}"),
    ERROR_CLIENT_SERVICE("Error in client service request with Phone Number: %s"),
    CLIENT_NOT_FOUND("Client not found: %s"),
    CLIENT_NOTIFICATION_NOT_FOUND("Client notifications not found: %s"),
    CLIENT_CONFIGURATION_NOTIFICATION_NOT_FOUND("Client configuration notifications not found: %s"),
    NOTIFICATION_NOT_FOUND("notification not found: %s"),
    DYNAMO_DB_ERROR("Error DynamoDB: {} {}"),
    SERVICE_EXCEPTION("Service Exception message {} - code {}"),
    UNSUPPORTED_NOTIFICATION("Unsupported notification type: {} {}"),
    NOTIFICATION_EXCEPTION("Notification exception: {}, {}"),
    CLIENT_RETROFIT_FOUND("The client was found successful"),
    SENDING_SMS_MESSAGE("Sending message to: {}"),
    CLIENT_CONFIG_NOTIFICATION_NOT_FOUND("Client config notifications not found: %s"),
    CLIENT_TRYING_CHANGE_DEVICE_NOTIFICATIONS("Client trying change device notifications: %s"),
    SNS_SDK_ERROR("Error SNS: {} {}"),
    UNSUPPORTED_DEVICE_PUSH_NOTIFICATION("Unsupported push notification device os: {} {}"),
    OTP_SEND_AUTHORIZATION_SAC("OTP Authorization sac sent to: {}"),
    OTP_VALIDATION("Otp received: {} Otp verify: {} "),
    ERROR_SEND_AUTHORIZATION_SAC("Error creating OTP for idClient {} when authorizing sac, type of transaction {}, with " +
            "error {}.");


    private String message;

    LogMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
