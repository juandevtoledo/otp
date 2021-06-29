package com.lulobank.otp.starter.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.HeadersMethodArgumentResolver;
import org.springframework.messaging.handler.annotation.support.PayloadArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Getter
public class AmazonSqsConfig {
  @Value("${cloud.aws.region.static}")
  private String amazonRegions;
  @Value("${cloud.aws.sqs.endpoint}")
  private String sqsEndPoint;
  @Value("${cloud.aws.max-number-of-messages}")
  private Integer maxNumberOfMessages;

  @Bean
  public QueueMessagingTemplate queueMessagingTemplate() {
    return new QueueMessagingTemplate(amazonSQSAsync());
  }

  public AmazonSQSAsync amazonSQSAsync() {
    return AmazonSQSAsyncClientBuilder.standard()
             .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsEndPoint, amazonRegions)).build();
  }

  @Bean
  public SimpleMessageListenerContainer simpleMessageListenerContainer() {
    SimpleMessageListenerContainer msgListenerContainer = simpleMessageListenerContainerFactory()
                                                            .createSimpleMessageListenerContainer();
    msgListenerContainer.setMessageHandler(queueMessageHandler());
    return msgListenerContainer;
  }

  @Bean
  public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
    SimpleMessageListenerContainerFactory msgListenerContainerFactory = new SimpleMessageListenerContainerFactory();
    msgListenerContainerFactory.setAmazonSqs(amazonSQSAsync());
    msgListenerContainerFactory.setMaxNumberOfMessages(maxNumberOfMessages);
    return msgListenerContainerFactory;
  }

  @Bean
  public QueueMessageHandler queueMessageHandler() {
    QueueMessageHandlerFactory queueMsgHandlerFactory = new QueueMessageHandlerFactory();
    queueMsgHandlerFactory.setAmazonSqs(amazonSQSAsync());
    QueueMessageHandler queueMessageHandler = queueMsgHandlerFactory.createQueueMessageHandler();
    List<HandlerMethodArgumentResolver> list = new ArrayList<>();
    list.add(new HeadersMethodArgumentResolver());
    list.add(new PayloadArgumentResolver(new MappingJackson2MessageConverter()));
    queueMessageHandler.setArgumentResolvers(list);
    return queueMessageHandler;
  }
}
