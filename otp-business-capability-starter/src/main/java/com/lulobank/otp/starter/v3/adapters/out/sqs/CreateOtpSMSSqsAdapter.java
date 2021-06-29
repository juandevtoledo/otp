package com.lulobank.otp.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.features.onboardingotp.model.OtpRequest;
import com.lulobank.otp.services.ports.out.messaging.dto.SMSNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
public class CreateOtpSMSSqsAdapter implements Action<Response<OtpResponse>, OtpRequest> {

    private final String clientAlertsQueueEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public void run(Response<OtpResponse> otpResponseResponse, OtpRequest otpRequest) {
        Event<SMSNotificationMessage> createOtpMessage =
                EventFactory.ofDefaults(buildSmsNotificationMessage(otpRequest, otpResponseResponse))
                .build();
        sqsBraveTemplate.convertAndSend(clientAlertsQueueEndpoint, createOtpMessage);
        log.info("Create otp message SMS for phone {} has been sent to queue {}", otpRequest.getPhoneNumber(),
                clientAlertsQueueEndpoint);
    }

    private SMSNotificationMessage buildSmsNotificationMessage(OtpRequest otpRequest, Response<OtpResponse> otpResponseResponse){
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("otp", otpResponseResponse.getContent().getOtp());
        return SMSNotificationMessage.builder()
                .notificationType("CREATE_OTP")
                .phonePrefix(otpRequest.getPrefix())
                .phoneNumber(otpRequest.getPhoneNumber())
                .attributes(attributes)
                .build();
    }
}
