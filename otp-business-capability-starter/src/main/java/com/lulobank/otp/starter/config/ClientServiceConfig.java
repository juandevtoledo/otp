package com.lulobank.otp.starter.config;

import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientServiceConfig {
  @Value("${services.clients.url}")
  private String serviceDomain;


  @Bean
  @Qualifier("retrofitClientOperations")
  public RetrofitClientOperations retrofitClientOperations() {
    return new RetrofitClientOperations(serviceDomain);
  }


}
