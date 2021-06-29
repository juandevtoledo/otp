package com.lulobank.otp.starter.config;

import com.lulobank.otp.services.actions.OtpOperationMessage;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OperationsConfig {
  @Bean
  @ConfigurationProperties("operations.messages")
  public Map<String, OtpOperationMessage> operationMessages() {
    return new HashMap<>();
  }

  @Bean
  public OtpService otpService() {
    return OtpService.getInstance();
  }
}
