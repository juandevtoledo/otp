package com.lulobank.otp.services.utils;

public enum MessageAttributeKey {
  AWS_SNS_SMS_SENDER_ID("AWS.SNS.SMS.SenderID"), AWS_SNS_SMS_MAX_PRICE("AWS.SNS.SMS.MaxPrice"), AWS_SNS_SMS_SMS_TYPE(
    "AWS.SNS.SMS.SMSType");
  private String key;

  MessageAttributeKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
