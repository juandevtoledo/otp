package com.lulobank.otp.services.inboundadapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClientContent;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByPhone;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByPhoneContent;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientsOperationsOAuth2;
import com.lulobank.core.events.Event;
import com.lulobank.core.helpers.GlobalConstants;
import com.lulobank.otp.sdk.dto.email.EmailAttachment;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import com.lulobank.otp.sdk.dto.events.EmailMessageNotification;
import com.lulobank.otp.sdk.dto.events.NewPushMessageNotification;
import com.lulobank.otp.sdk.dto.events.NewSMSMessageNotification;
import com.lulobank.otp.sdk.dto.notifications.DeviceOs;
import com.lulobank.otp.services.outbounadadapters.services.IMessageToSend;
import com.lulobank.otp.services.outbounadadapters.services.SESEmailSender;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import com.lulobank.otp.services.ports.service.FileService;
import com.lulobank.otp.services.utils.EventUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SqsNotificationsListenerAdapterTest {
  private static final String CLIENT_ID_FROM = "e103f62f-319b-4f5b-9646-af88e6ee720f";
  private static final String CLIENT_ID_TO = "e103f62f-319b-4f5b-9646-af88e6ee999f";
  private static final String NOTIFICATION_ID = "e103f62f-319b-1234-9646-af88e6ee999f";
  private static final Integer PREFIX = 57;
  private static final String PHONE_NUMBER_FROM = "573192563822";
  private static final String PHONE_NUMBER_TO = "573192562222";
  private static final String MESSAGE_BODY = "Test";
  private static final String TITLE = "Title";
  private static final String ACTION = "action";
  private static final String FCM_TOKEN = "452dfs2d4f25ds4f25d4f25df25ds5";
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final String JSON_IOS_GCM_PUSH = "{\"GCM\":\"{\\\"notification\\\":{\\\"title\\\":\\\"Title\\\",\\\"body\\\":\\\"Test\\\",\\\"click_action\\\":\\\"action\\\"},\\\"data\\\":{\\\"idClient\\\":\\\"e103f62f-319b-4f5b-9646-af88e6ee999f\\\",\\\"idNotification\\\":\\\"e103f62f-319b-1234-9646-af88e6ee999f\\\"},\\\"content-available\\\":true,\\\"priority\\\":\\\"high\\\",\\\"mutable-content\\\":true}\"}";
  private static final String JSON_ANDROID_GCM_PUSH = "{\"GCM\":\"{\\\"data\\\":{\\\"idClient\\\":\\\"e103f62f-319b-4f5b-9646-af88e6ee999f\\\",\\\"action\\\":\\\"action\\\",\\\"idNotification\\\":\\\"e103f62f-319b-1234-9646-af88e6ee999f\\\",\\\"title\\\":\\\"Title\\\",\\\"body\\\":\\\"Test\\\"}}\"}";

  private SqsNotificationsListenerAdapter testedClass;
  @Mock
  private SMSMessageSender smsMessageSender;
  @Mock
  private SESEmailSender sesEmailSender;
  @Mock
  private RetrofitClientOperations retrofitClientOperations;
  @Mock
  private RetrofitClientsOperationsOAuth2 retrofitClientOperationsInternal;
  @Mock
  private PushNotificationService pushNotificationService;
  @Mock
  private FileService fileService;

  private EmailTemplate emailTemplate;
  private ObjectMapper objectMapper;
  private ClientInformationByPhone clientInformationByPhoneFrom;
  private ClientInformationByPhone clientInformationByPhoneTo;
  private ClientInformationByPhoneContent clientInformationByPhoneContentFrom;
  private ClientInformationByPhoneContent clientInformationByPhoneContentTo;
  private ClientInformationByIdClient clientInformationByIdClient;
  @Captor
  private ArgumentCaptor<String> stringArgumentCaptor;

  @Captor
  private ArgumentCaptor<NewPushMessageNotification> newPushMessageNotificationArgumentCaptor;

  private NewPushMessageNotification newPushMessageNotification;

  private Map<String, Object> headers;

  private Map<String, String> retrofitHeaders;


  @Before
  public void init() throws IOException {
    MockitoAnnotations.initMocks(this);
    objectMapper = new ObjectMapper();
    testedClass = new SqsNotificationsListenerAdapter(smsMessageSender, sesEmailSender,pushNotificationService,
            fileService);
    emailTemplate = new EmailTemplate();
    emailTemplate.setFrom("mail@mail.com");
    emailTemplate.setBody(GlobalConstants.EMPTY_STRING);
    emailTemplate.setSubject(GlobalConstants.EMPTY_STRING);
    clientInformationByPhoneFrom = new ClientInformationByPhone();
    clientInformationByPhoneContentFrom = new ClientInformationByPhoneContent();
    clientInformationByPhoneContentFrom.setIdClient(CLIENT_ID_FROM);
    clientInformationByPhoneFrom.setContent(clientInformationByPhoneContentFrom);
    clientInformationByPhoneTo = new ClientInformationByPhone();
    clientInformationByPhoneContentTo = new ClientInformationByPhoneContent();
    clientInformationByPhoneContentTo.setIdClient(CLIENT_ID_TO);
    clientInformationByPhoneTo.setContent(clientInformationByPhoneContentTo);


    clientInformationByIdClient = new ClientInformationByIdClient();
    ClientInformationByIdClientContent clientInformationByIdClientContent = new ClientInformationByIdClientContent();
    clientInformationByIdClientContent.setIdClient(CLIENT_ID_FROM);
    clientInformationByIdClientContent.setAddress("Street Test");
    clientInformationByIdClientContent.setEmailAddress("mail@mail.com");
    clientInformationByIdClientContent.setPhonePrefix(57);
    clientInformationByIdClientContent.setPhoneNumber("1234567890");
    clientInformationByIdClient.setContent(clientInformationByIdClientContent);

    headers = new HashMap<>();
    headers.put("authenticationToken", "fakeToken");

    retrofitHeaders = new HashMap<>();
    retrofitHeaders.put("authenticationToken", "fakeToken");


    newPushMessageNotification = new NewPushMessageNotification();
    newPushMessageNotification.setOsDevice(DeviceOs.ANDROID.name());
    newPushMessageNotification.setIdClient(CLIENT_ID_TO);
    newPushMessageNotification.setTitle(TITLE);
    newPushMessageNotification.setMessageBody(MESSAGE_BODY);
    newPushMessageNotification.setArnSns(FCM_TOKEN);
    newPushMessageNotification.setIdNotification(NOTIFICATION_ID);
    newPushMessageNotification.setAction(ACTION);

  }

  @Test
  public void messageReadAndSent() throws JsonProcessingException {
    NewSMSMessageNotification message = new NewSMSMessageNotification();
    message.setMessage("Test Message");
    message.setPhoneNumber("3228591725");
    message.setPhonePrefix("57");
    Event<NewSMSMessageNotification> event = new EventUtils().getEvent(message);
    testedClass.getMessage(headers, objectMapper.writeValueAsString(event));
    verify(smsMessageSender, times(1)).sendMessage(any(IMessageToSend.class));
  }

  @Test
  public void Plain_Email_Read_And_Sent() throws JsonProcessingException {
    EmailMessageNotification message = new EmailMessageNotification();
    message.setEmailTemplate(emailTemplate);
    Event<EmailMessageNotification> event = new EventUtils().getEvent(message);
    testedClass.getMessage(headers, objectMapper.writeValueAsString(event));
    verify(sesEmailSender, times(1)).sendEmail(any(EmailTemplate.class));
  }

  @Test
  public void Raw_Email_Read_And_Sent() throws JsonProcessingException {
    EmailMessageNotification message = new EmailMessageNotification();
    emailTemplate.getFiles().add(new EmailAttachment("", new byte[0], "application/pdf"));
    message.setEmailTemplate(emailTemplate);
    Event<EmailMessageNotification> event = new EventUtils().getEvent(message);
    testedClass.getMessage(headers, objectMapper.writeValueAsString(event));
    verify(sesEmailSender, times(1)).sendEmail(any(EmailTemplate.class));
  }

  @Test
  public void newPushNotificationEventAndroid() throws JsonProcessingException {

    doNothing().when(pushNotificationService).sendPushNotification(any());
    Event<NewPushMessageNotification> event = new EventUtils().getEvent(newPushMessageNotification);
    testedClass.getMessage(headers, objectMapper.writeValueAsString(event));
    verify(pushNotificationService).sendPushNotification(newPushMessageNotificationArgumentCaptor.capture());
    assertEquals(FCM_TOKEN, newPushMessageNotificationArgumentCaptor.getValue().getArnSns());
  }

  @Test
  public void newPushNotificationEventIOS() throws JsonProcessingException {
    newPushMessageNotification.setOsDevice(DeviceOs.IOS.name());
    doNothing().when(pushNotificationService).sendPushNotification(any());
    Event<NewPushMessageNotification> event = new EventUtils().getEvent(newPushMessageNotification);
    testedClass.getMessage(headers, objectMapper.writeValueAsString(event));
    verify(pushNotificationService).sendPushNotification(newPushMessageNotificationArgumentCaptor.capture());
    assertEquals(FCM_TOKEN, newPushMessageNotificationArgumentCaptor.getValue().getArnSns());
  }

  @Test
  public void newPushNotificationEventIOSThrowSDKClientException() throws JsonProcessingException {
    doThrow(new NullPointerException()).when(pushNotificationService).sendPushNotification(any());
    Event<NewPushMessageNotification> event = new EventUtils().getEvent(newPushMessageNotification);
    testedClass.getMessage(headers, objectMapper.writeValueAsString(event));

  }

}
