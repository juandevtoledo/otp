package com.lulobank.otp.starter.v3.adapters.out.notifications;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.IClientInformationOperations;
import com.lulobank.clients.sdk.operations.dto.onboardingclients.ClientInformationByIdClient;
import com.lulobank.otp.services.outbounadadapters.services.SESEmailSender;
import com.lulobank.otp.services.ports.out.messaging.dto.EmailNotificationMessage;
import com.lulobank.otp.services.ports.out.messaging.dto.SMSNotificationMessage;
import com.lulobank.otp.services.v3.domain.OTPArrangement;
import com.lulobank.otp.services.v3.domain.OTPChannel;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import com.lulobank.otp.services.v3.port.out.notifactions.ClientGetInfoService;
import com.lulobank.otp.services.v3.port.out.notifactions.NotifyService;
import com.lulobank.otp.starter.v3.adapters.Sample;
import com.lulobank.otp.starter.v3.adapters.out.sqs.NotifyEmailSqsAdapter;
import com.lulobank.otp.starter.v3.adapters.out.sqs.NotifySMSSqsAdapter;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static com.lulobank.otp.services.v3.domain.OTPTransaction.TRANSFER;
import static com.lulobank.otp.starter.Constant.PHONE_NUMBER;
import static com.lulobank.otp.starter.Constant.PREFIX;
import static com.lulobank.otp.starter.Constant.EMAIL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class NotifyServiceImplTest {

    private NotifyService notifyServiceImpl;

    private final ArgumentCaptor<PublishRequest> publishReqAC = ArgumentCaptor.forClass(PublishRequest.class);

    private AmazonSNSAsync amazonSNSAsync;

    private AmazonSimpleEmailService amazonSimpleEmailService;

    private NotifyEmailSqsAdapter notifyEmailSqsAdapter;

    private NotifySMSSqsAdapter notifySMSSqsAdapter;

    private final ArgumentCaptor<SendEmailRequest> emailAC = ArgumentCaptor.forClass(SendEmailRequest.class);

    private final ArgumentCaptor<EmailNotificationMessage> emailNotificationMessageCaptor =
            ArgumentCaptor.forClass(EmailNotificationMessage.class);

    private final ArgumentCaptor<SMSNotificationMessage> smsNotificationMessageCaptor =
            ArgumentCaptor.forClass(SMSNotificationMessage.class);


    private IClientInformationOperations clientInformationOperations;

    @Before
    public void setUp() {
        clientInformationOperations = mock(IClientInformationOperations.class);
        ClientGetInfoService clientGetInfoService = new ClientGetInfoServiceImpl(clientInformationOperations);
        amazonSNSAsync = mock(AmazonSNSAsync.class);
        amazonSimpleEmailService = mock(AmazonSimpleEmailService.class);
        SESEmailSender sesEmailSender = new SESEmailSender(amazonSimpleEmailService);
        notifySMSSqsAdapter = mock(NotifySMSSqsAdapter.class);
        notifyEmailSqsAdapter = mock(NotifyEmailSqsAdapter.class);
        notifyServiceImpl = new NotifyServiceImpl(clientGetInfoService,
                notifySMSSqsAdapter, notifyEmailSqsAdapter);
        ClientInformationByIdClient clientInfo = new ClientInformationByIdClient();
        clientInfo.setPhoneNumber(PHONE_NUMBER);
        clientInfo.setPhonePrefix(PREFIX);
        clientInfo.setEmailAddress(EMAIL);
        when(clientInformationOperations.getAllClientInformationByIdClient(anyMap(), anyString())).thenReturn(clientInfo);
    }

    @Test
    public void sendOtpWhenUserExistsToDefaultChannel() {
        Map<String, String> headers = new HashMap<>();
        OTPValidationRq rq = createRequest(List.of(OTPChannel.SMS, OTPChannel.MAIL));
        notifyServiceImpl.notify(headers, "0000", "client-id-key", rq.getTransactionType(),rq.getPayload()).get();
        // INTERACTIONS SMS
        verify(notifySMSSqsAdapter).sendMessage(smsNotificationMessageCaptor.capture());
        assertThat(smsNotificationMessageCaptor.getValue().getPhoneNumber(), is(equalTo(PHONE_NUMBER)));
        assertThat(smsNotificationMessageCaptor.getValue().getNotificationType(), is(equalTo(TRANSFER.name())));

        // INTERACTIONS EMAIL
        verify(notifyEmailSqsAdapter, atLeastOnce()).sendMessage(any());
        verify(notifyEmailSqsAdapter).sendMessage(emailNotificationMessageCaptor.capture());
        assertThat(emailNotificationMessageCaptor.getValue().getNotificationType(), is(equalTo(TRANSFER.name())));
        assertThat(emailNotificationMessageCaptor.getValue().getTo(), is(equalTo(EMAIL)));
    }


    @Test
    public void sendOtpWhenUserExistsToUIChannel() {
        Map<String, String> headers = new HashMap<>();
        OTPValidationRq rq = createRequest(List.of(OTPChannel.UI));

        notifyServiceImpl.notify(headers, "0000", "client-id-key", rq.getTransactionType(),rq.getPayload()).get();

        verify(amazonSNSAsync, times(0)).publishAsync(Mockito.any());
        verify(amazonSimpleEmailService, times(0)).sendEmail(any());
    }


    @Test
    public void sendOtpWhenUserNotExists() {
        Map<String, String> headers = new HashMap<>();
        OTPValidationRq rq = createRequest(List.of(OTPChannel.SMS, OTPChannel.MAIL));

        when(clientInformationOperations.getAllClientInformationByIdClient(anyMap(), any())).thenThrow(
                new RuntimeException("Network Error")
        );
        notifyServiceImpl.notify(headers, "0000", "client-id-key", rq.getTransactionType(),rq.getPayload()).get();

        verify(amazonSNSAsync, times(0)).publishAsync(Mockito.any());
        verify(amazonSimpleEmailService, times(0)).sendEmail(any());
    }

    @Test
    public void sendOtpWhenUserExistsToEmailChannelNewEmail() {
        Map<String, String> headers = new HashMap<>();
        OTPValidationRq rq = createRequestNewEmail(List.of(OTPChannel.MAIL));

        notifyServiceImpl.notify(headers, "0000", "client-id-key", rq.getTransactionType(),rq.getPayload()).get();

        verify(notifyEmailSqsAdapter, times(1)).sendMessage(emailNotificationMessageCaptor.capture());
        assertThat(emailNotificationMessageCaptor.getValue().getTo(), is(equalTo("mail@mail.com")));
    }


    @NotNull
    private OTPValidationRq createRequest(List<OTPChannel> channels) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = Try.of(() -> mapper.readTree("{\"target\":\"ddddd\",\"amount\":30000.0}")).get();
        OTPValidationRq rq = new OTPValidationRq(transferJson, "client-id-key", OTPTransaction.TRANSFER);
        OTPArrangement arrangement = rq.getTransactionType().getArrangement();
        arrangement.setTargets(channels);
        return rq;
    }

    @NotNull
    private OTPValidationRq createRequestNewEmail(List<OTPChannel> channels) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = Try.of(() -> mapper.readTree("{\"newEmail\":\"mail@mail.com\"}")).get();
        OTPValidationRq rq = new OTPValidationRq(transferJson, "client-id-key", OTPTransaction.UPDATE_EMAIL);
        OTPArrangement arrangement = rq.getTransactionType().getArrangement();
        arrangement.setTargets(channels);
        return rq;
    }

    @Test
    public void sendOtpMailWhenAuthorizationSac() {
        notifyServiceImpl.notifyAuthorizationSac("0000", Sample.getClientNotifyInfo(),"msg");
        verify(notifyEmailSqsAdapter, timeout(100).atLeastOnce()).sendMessage(emailNotificationMessageCaptor.capture());
        assertThat(emailNotificationMessageCaptor.getValue().getTo(), is(equalTo("mail@mail.com")));
        assertThat(emailNotificationMessageCaptor.getValue().getNotificationType(), is(equalTo("msg")));
    }


}
