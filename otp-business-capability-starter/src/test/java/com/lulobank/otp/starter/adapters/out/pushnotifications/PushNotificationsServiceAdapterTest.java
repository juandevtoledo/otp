package com.lulobank.otp.starter.adapters.out.pushnotifications;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.otp.sdk.dto.events.NewPushMessageNotification;
import com.lulobank.otp.sdk.dto.notifications.DeviceOs;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.lulobank.otp.starter.utils.PushNotificationsResponseSample.getFirebaseResponseError;
import static com.lulobank.otp.starter.utils.PushNotificationsResponseSample.getFirebaseResponseOK;
import static com.lulobank.otp.starter.utils.PushNotificationsResponseSample.getRequestAndroidNotificationFirebase;
import static com.lulobank.otp.starter.utils.PushNotificationsResponseSample.getRequestIOSNotificationFirebase;
import static org.springframework.http.HttpStatus.OK;

public class PushNotificationsServiceAdapterTest {

    private static final String tokenFirebase = "123456789";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PushNotificationService pushNotificationService;
    private final ObjectMapper mapper = new ObjectMapper();
    private NewPushMessageNotification messageNotification = buildNewPushMessageNotification();

    @Before
    public void setUp() {
        pushNotificationService = new PushNotificationsServiceAdapter("http://localhost:8981", tokenFirebase);
    }

    @Test
    public void sendANDROIDNotification() throws IOException {
        String responseStr = mapper.writeValueAsString(getFirebaseResponseOK());
        wireMockRule.stubFor(post(urlEqualTo("/fcm/send"))
                .willReturn(aResponse().withStatus(OK.value()).withBody(responseStr)));
        pushNotificationService.sendPushNotification(messageNotification);
        verify(1, postRequestedFor(urlEqualTo("/fcm/send"))
                .withRequestBody(equalToJson(getRequestAndroidNotificationFirebase())));

    }



    @Test
    public void sendIOSNotification() throws IOException {
        messageNotification.setOsDevice(DeviceOs.IOS.name());
        String responseStr = mapper.writeValueAsString(getFirebaseResponseOK());
        wireMockRule.stubFor(post(urlEqualTo("/fcm/send"))
                .willReturn(aResponse().withStatus(OK.value()).withBody(responseStr)));
        pushNotificationService.sendPushNotification(messageNotification);
        verify(1, postRequestedFor(urlEqualTo("/fcm/send"))
                .withRequestBody(equalToJson(getRequestIOSNotificationFirebase())));
    }

    @Test
    public void errorSendingNotification() throws IOException {
        String responseStr = mapper.writeValueAsString(getFirebaseResponseError());
        wireMockRule.stubFor(post(urlEqualTo("/fcm/send"))
                .willReturn(aResponse().withStatus(OK.value()).withBody(responseStr)));
        pushNotificationService.sendPushNotification(messageNotification);
        verify(1, postRequestedFor(urlEqualTo("/fcm/send"))
                .withRequestBody(equalToJson(getRequestAndroidNotificationFirebase())));
    }

    private NewPushMessageNotification buildNewPushMessageNotification() {
        NewPushMessageNotification messageNotification = new NewPushMessageNotification();
        messageNotification.setAction("action");
        messageNotification.setArnSns("token");
        messageNotification.setIdClient("123");
        messageNotification.setIdNotification("456");
        messageNotification.setMessageBody("body");
        messageNotification.setTitle("title");
        messageNotification.setOsDevice(DeviceOs.ANDROID.name());
        return messageNotification;
    }


}
