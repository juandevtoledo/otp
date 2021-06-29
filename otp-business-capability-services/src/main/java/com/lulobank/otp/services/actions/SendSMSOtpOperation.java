package com.lulobank.otp.services.actions;

import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.otp.sdk.dto.OperationResponse;
import com.lulobank.otp.services.features.otp.GenerateOtpForNewLoanCommand;
import com.lulobank.otp.services.ports.out.messaging.MessagingPort;
import com.lulobank.otp.services.ports.out.messaging.dto.SMSNotificationMessage;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;

@RequiredArgsConstructor
public class SendSMSOtpOperation implements Action<Response<OperationResponse>, GenerateOtpForNewLoanCommand> {

    private final MessagingPort<SMSNotificationMessage> messagingPort;

    @Override
    public void run(Response<OperationResponse> operationResponseResponse,
                    GenerateOtpForNewLoanCommand generateOtpForNewLoanCommand) {
        messagingPort.sendMessage(buildSmsNotificationMessage(operationResponseResponse.getContent()));
    }

    private SMSNotificationMessage buildSmsNotificationMessage(OperationResponse response){
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("otp", response.getChallenge());
        return SMSNotificationMessage.builder()
                .clientId(Strings.EMPTY)
                .notificationType("CREATE_HIRED_OTP")
                .phonePrefix(response.getPrefix())
                .phoneNumber(response.getPhone())
                .attributes(attributes)
                .build();
    }
}
