package com.lulobank.otp.starter.config;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageAttribute;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AmazonSnsConfig {
  @Value("${cloud.aws.region.static}")
  private String amazonRegions;
  @Value("${cloud.aws.sns.sender-id}")
  private String senderId;
  @Value("${cloud.aws.sns.max-price}")
  private String maxPrice;
  @Value("${cloud.aws.sns.type}")
  private String type;

  @Bean
  public SMSMessageSender smsMessageService() {
    return new SMSMessageSender(creteAmazonSNSAsync(), smsMessageAttributes());
  }

  public SMSMessageAttribute smsMessageAttributes() {
    return new SMSMessageAttribute(senderId, maxPrice, type);
  }

  @Bean
  @Primary
  public AmazonSNSAsync creteAmazonSNSAsync() {
    return AmazonSNSAsyncClientBuilder.standard().withRegion(Regions.fromName(amazonRegions)).build();
  }
}
