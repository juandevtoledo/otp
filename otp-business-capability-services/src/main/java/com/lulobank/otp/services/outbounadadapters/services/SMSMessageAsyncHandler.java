package com.lulobank.otp.services.outbounadadapters.services;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMSMessageAsyncHandler implements AsyncHandler<PublishRequest, PublishResult> {
  private final Logger logger;

  public SMSMessageAsyncHandler(Class loggerClassProcess) {
    logger = LoggerFactory.getLogger(loggerClassProcess);
  }

  @Override
  public void onError(Exception exception) {
    logger.error("Sending SMS Message to the Queue ", exception);
  }

  @Override
  public void onSuccess(PublishRequest request, PublishResult publishResult) {
    if (logger.isTraceEnabled()) {
      logger.trace("Message Send it to the queue {}", publishResult.getMessageId());
    }
  }
}
