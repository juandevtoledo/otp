package com.lulobank.otp.starter.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.starter.adapters.out.pushnotifications.dto.FirebaseResponse;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.util.ResourceUtils.getFile;

public class PushNotificationsResponseSample {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static FirebaseResponse getFirebaseResponseOK() throws IOException {
        return objectMapper.readValue(getFile("classpath:firebase/firebase_ok.json"), FirebaseResponse.class);
    }

    public static FirebaseResponse getFirebaseResponseError() throws IOException {
        return objectMapper.readValue(getFile("classpath:firebase/firebase_error.json"), FirebaseResponse.class);
    }


    public static String getRequestAndroidNotificationFirebase() throws IOException {
        return FileUtils.readFileToString(
                getFile("classpath:firebase/requestAndroidNotification.json"), StandardCharsets.UTF_8);
    }

    public static String getRequestIOSNotificationFirebase() throws IOException {
        return FileUtils.readFileToString(
                getFile("classpath:firebase/requestIOSNotification.json"), StandardCharsets.UTF_8);
    }
}
