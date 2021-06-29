package com.lulobank.otp.starter.config;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import com.lulobank.otp.services.outbounadadapters.services.SESEmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonSesConfig {

    @Value("${cloud.aws.region.static}")
    private String amazonRegions;

    @Bean
    public MailSender emailMessageService() {
        return new SESEmailSender(createAmazonSESService());
    }

    public AmazonSimpleEmailService createAmazonSESService() {
        return AmazonSimpleEmailServiceClientBuilder.standard().withRegion(amazonRegions).build();
    }
}
