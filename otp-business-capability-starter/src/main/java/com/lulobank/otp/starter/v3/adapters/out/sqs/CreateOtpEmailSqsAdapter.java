package com.lulobank.otp.starter.v3.adapters.out.sqs;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.events.api.Event;
import com.lulobank.events.api.EventFactory;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.features.onboardingotp.model.OtpEmailRequest;
import com.lulobank.otp.services.ports.out.messaging.dto.EmailNotificationMessage;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
public class CreateOtpEmailSqsAdapter implements Action<Response<OtpResponse>, OtpEmailRequest> {

    private final String clientAlertsQueueEndpoint;
    private final SqsBraveTemplate sqsBraveTemplate;

    @Override
    public void run(Response<OtpResponse> otpResponseResponse, OtpEmailRequest otpEmailRequest) {
        String email = getEmail(otpEmailRequest);
        Event<EmailNotificationMessage> createOtpMessage = EventFactory.ofDefaults(
                buildEmailNotificationMessage(otpResponseResponse,
                        email))
                .build();
        sqsBraveTemplate.convertAndSend(clientAlertsQueueEndpoint, createOtpMessage);
        log.info("Create otp message Email for email {} has been sent to queue {}", email, clientAlertsQueueEndpoint);
    }

    private EmailNotificationMessage buildEmailNotificationMessage(Response<OtpResponse> otpResponseResponse,
                                                                   String email){
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("otp", otpResponseResponse.getContent().getOtp());
        return EmailNotificationMessage.builder()
                .notificationType("CREATE_OTP")
                .to(email)
                .attributes(attributes)
                .build();
    }

    private String getEmail(OtpEmailRequest otpEmailRequest){
        return Try.of(()-> otpEmailRequest.getEmailTemplate().getTo())
                .map(to-> to.stream().findFirst()
                        .orElse(Strings.EMPTY)).get();
    }
}
