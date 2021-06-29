package com.lulobank.otp.starter.v3.adapters.out.notifications;

import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NotifyServiceSMS {

    @Value("${cloud.aws.sns.sender-id}")
    private String senderId;
    @Value("${cloud.aws.sns.max-price}")
    private String maxPrice;
    @Value("${cloud.aws.sns.type}")
    private String type;

    private AmazonSNSAsync amazonSNSAsync;

    @Autowired
    public NotifyServiceSMS(AmazonSNSAsync amazonSNSAsync) {
        this.amazonSNSAsync = amazonSNSAsync;
    }

    public Try<Boolean> sendMessage(String message, String phone, String otp) {
        log.info("Send OTP SMS to {}", phone);
        PublishRequest request = new PublishRequest();
        String userMessage = String.format(message, otp);
        request.withMessage(userMessage).withPhoneNumber(phone)
                .withMessageAttributes(messageAttributes());

        return Try.of(() -> {
            amazonSNSAsync.publishAsync(request);
            return true;
        });
    }

    private Map<String, MessageAttributeValue> messageAttributes() {
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue().withStringValue(senderId)
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue().withStringValue(maxPrice)
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue().withStringValue(type)
                .withDataType("String"));
        return smsAttributes;
    }


}
