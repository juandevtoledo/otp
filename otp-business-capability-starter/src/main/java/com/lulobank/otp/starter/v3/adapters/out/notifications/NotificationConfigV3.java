package com.lulobank.otp.starter.v3.adapters.out.notifications;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.lulobank.clients.sdk.operations.IClientInformationOperations;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientInformationOperations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfigV3 {

    @Value("${services.clients.url}")
    private String serviceDomain;

    @Bean
    public NotifyServiceSMS notifyServiceBean(AmazonSNSAsync amazonSNSAsync) {
        return new NotifyServiceSMS(amazonSNSAsync);
    }

    @Bean
    public ClientGetInfoServiceImpl clientGetInfoService(){
        IClientInformationOperations operations = new RetrofitClientInformationOperations(serviceDomain);
        return new ClientGetInfoServiceImpl(operations);
    }
}
