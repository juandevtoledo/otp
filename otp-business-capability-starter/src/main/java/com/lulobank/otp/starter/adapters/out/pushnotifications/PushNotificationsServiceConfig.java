package com.lulobank.otp.starter.adapters.out.pushnotifications;

import com.lulobank.otp.services.ports.out.PushNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PushNotificationsServiceConfig {

    @Value("${cloud.google.firebase.push-endpoint}")
    private String endpointPushFirebase;


    @Value("${cloud.google.firebase.push-key}")
    private String serverKeyFirebase;


    @Bean
    public PushNotificationService firebaseMessaging(){
        return new PushNotificationsServiceAdapter(endpointPushFirebase,serverKeyFirebase);
    }

}
