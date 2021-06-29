package com.lulobank.otp.services.outbounadadapters.services;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.lulobank.otp.services.utils.DataType;
import com.lulobank.otp.services.utils.MessageAttributeKey;

import java.util.HashMap;
import java.util.Map;

public class SMSMessageSender implements IMessageSender {
  private AmazonSNSAsync amazonSNSAsync;
  private SMSMessageAttribute smsMessageAttribute;

  public SMSMessageSender(AmazonSNSAsync amazonSNSAsync, SMSMessageAttribute smsMessageAttribute) {
    this.amazonSNSAsync = amazonSNSAsync;
    this.smsMessageAttribute = smsMessageAttribute;
  }

  @Override
  public void sendMessage(IMessageToSend messageToSend) {
    PublishRequest request = new PublishRequest();
    request.withMessage(messageToSend.getMessage()).withPhoneNumber(messageToSend.getPhone())
      .withMessageAttributes(messageAttributes());
    // TODO: Como administrar reintentos y circuit breaker
    amazonSNSAsync.publishAsync(request, new SMSMessageAsyncHandler(SMSMessageSender.class));
  }

  private Map<String, MessageAttributeValue> messageAttributes() {
    Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
    smsAttributes.put(MessageAttributeKey.AWS_SNS_SMS_SENDER_ID.getKey(), new MessageAttributeValue().withStringValue(
      smsMessageAttribute.getSenderId()) //The sender ID shown on the device.
                                                                            .withDataType(DataType.STRING.getType()));
    smsAttributes.put(MessageAttributeKey.AWS_SNS_SMS_MAX_PRICE.getKey(),
      new MessageAttributeValue().withStringValue(smsMessageAttribute.getMaxPrice()) //Sets the max price to 0.50 USD.
        .withDataType(DataType.NUMBER.getType()));
    smsAttributes.put(MessageAttributeKey.AWS_SNS_SMS_SMS_TYPE.getKey(),
      new MessageAttributeValue().withStringValue(smsMessageAttribute.getType()) //Sets the type to promotional.
        .withDataType(DataType.STRING.getType()));
    return smsAttributes;
  }
}
