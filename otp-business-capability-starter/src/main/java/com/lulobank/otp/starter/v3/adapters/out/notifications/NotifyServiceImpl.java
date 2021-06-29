package com.lulobank.otp.starter.v3.adapters.out.notifications;

import com.fasterxml.jackson.databind.JsonNode;
import com.lulobank.otp.services.exceptions.NotValidDistributionChannelSupportedException;
import com.lulobank.otp.services.ports.out.messaging.MessagingPort;
import com.lulobank.otp.services.ports.out.messaging.dto.EmailNotificationMessage;
import com.lulobank.otp.services.ports.out.messaging.dto.SMSNotificationMessage;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.domain.OTPChannel;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.domain.clients.UpdateEmail;
import com.lulobank.otp.services.v3.domain.clients.UpdatePhoneNumber;
import com.lulobank.otp.services.v3.port.out.notifactions.ClientGetInfoService;
import com.lulobank.otp.services.v3.port.out.notifactions.NotifyService;
import io.vavr.API;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.lulobank.otp.services.v3.domain.OTPChannel.MAIL;
import static com.lulobank.otp.services.v3.domain.OTPChannel.SMS;
import static io.vavr.API.$;
import static io.vavr.API.Case;

@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService {

    private ClientGetInfoService clientGetInfoService;

    private final MessagingPort<SMSNotificationMessage> smsNotificationMessage;
    private final MessagingPort<EmailNotificationMessage> emailNotificationMessage;

    @Autowired
    public NotifyServiceImpl(ClientGetInfoService clientGetInfoService,
                             MessagingPort<SMSNotificationMessage> smsNotificationMessage,
                             MessagingPort<EmailNotificationMessage> emailNotificationMessage) {
        this.clientGetInfoService = clientGetInfoService;
        this.smsNotificationMessage = smsNotificationMessage;
        this.emailNotificationMessage = emailNotificationMessage;
    }


    @Override
    public Future<List<Try<Boolean>>> notify(Map<String, String> headers, String otp, String clientId,
                                             OTPTransaction transactionType, JsonNode payload) {
        return Future.of(() -> clientGetInfoService.get(headers, clientId)
                .map(clientInfo -> sendOtp(checkIfUpdateEmail(clientInfo,transactionType, payload), transactionType, otp))
                .getOrNull());
    }

    @Override
    public void notifyAuthorizationSac(String otp, ClientNotifyInfo clientNotifyInfo, String message) {
        Future.run(() -> dispatchEmail(message,otp,clientNotifyInfo));
    }

    private List<Try<Boolean>> sendOtp(ClientNotifyInfo clientInfo, OTPTransaction transactionType, String otp) {
        return transactionType.getArrangement().getTargets().map(
                channel -> dispatchMessage(channel, clientInfo, otp, transactionType)
        );
    }

    private Try<Boolean> dispatchMessage(OTPChannel channel, ClientNotifyInfo info, String otp, OTPTransaction transactionType) {
        return API.Match(channel).of(
                Case($(SMS), () -> dispatchSMS(transactionType.name(), otp, info)),
                Case($(MAIL), () -> dispatchEmail(transactionType.name(), otp, info)),
                Case($(), Try.failure(new NotValidDistributionChannelSupportedException(channel.name())))
        );
    }

    private Try<Boolean> dispatchSMS(String notificationType, String otp, ClientNotifyInfo info) {
        return Try.run(()->smsNotificationMessage.sendMessage(buildSmsNotificationMessage(otp, notificationType, info)))
                .map(response-> Boolean.TRUE);
    }

    private Try<Boolean> dispatchEmail(String notificationType, String otp, ClientNotifyInfo info) {
        return Try.run(()-> emailNotificationMessage.sendMessage(buildEmailNotificationMessage(notificationType, info.getEmail(), otp)))
                .map(response-> Boolean.TRUE);
    }

    private EmailNotificationMessage buildEmailNotificationMessage(String notificationType,
                                                                   String email,
                                                                   String otp){
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("otp", otp);
        return EmailNotificationMessage.builder()
                .notificationType(notificationType)
                .to(email)
                .attributes(attributes)
                .build();
    }

    private SMSNotificationMessage buildSmsNotificationMessage(String otp,String transactionType,ClientNotifyInfo clientNotifyInfo){
        HashMap<String, Object> attributes = new HashMap<>();
        attributes.put("otp", otp);
        return SMSNotificationMessage.builder()
                .notificationType(transactionType)
                .phonePrefix(String.valueOf(clientNotifyInfo.getPrefix()))
                .phoneNumber(clientNotifyInfo.getPhone())
                .attributes(attributes)
                .build();
    }

    private ClientNotifyInfo checkIfUpdateEmail(ClientNotifyInfo clientInfo, OTPTransaction transactionType,
                                                JsonNode payload) {

        return API.Match(transactionType).of(
                Case($(OTPTransaction.UPDATE_EMAIL), () -> getNewEmail(clientInfo,transactionType,payload)),
                Case($(OTPTransaction.UPDATE_PHONE_NUMBER), () -> getNewPhone(clientInfo,transactionType,payload)),
                Case($(), () -> clientInfo));
    }

    private ClientNotifyInfo getNewEmail(ClientNotifyInfo clientInfo, OTPTransaction transactionType, JsonNode payload) {
        return transactionType.toTransactionData(payload)
                .map(otpTransactionData -> (UpdateEmail)otpTransactionData)
                .map(UpdateEmail::getNewEmail)
                .peek(clientInfo::setEmail)
                .map(s -> clientInfo)
                .getOrElse(clientInfo);
    }

    private ClientNotifyInfo getNewPhone(ClientNotifyInfo clientInfo, OTPTransaction transactionType, JsonNode payload) {
        return transactionType.toTransactionData(payload)
                .map(otpTransactionData -> (UpdatePhoneNumber)otpTransactionData)
                .map(updatePhoneNumber -> {
                    clientInfo.setPhone(updatePhoneNumber.getNewPhoneNumber());
                    clientInfo.setPrefix(updatePhoneNumber.getCountryCode());
                    return clientInfo;
                })
                .getOrElse(clientInfo);
    }
}

