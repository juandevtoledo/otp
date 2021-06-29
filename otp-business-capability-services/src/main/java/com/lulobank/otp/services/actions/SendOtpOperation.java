package com.lulobank.otp.services.actions;

import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.otp.sdk.dto.OperationResponse;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpMessageToSend;
import com.lulobank.otp.services.features.otp.GenerateOtpForNewLoanCommand;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.utils.OtpUtils;

public class SendOtpOperation implements Action<Response<OperationResponse>, GenerateOtpForNewLoanCommand> {
  private SMSMessageSender smsMessageSender;

  public SendOtpOperation(SMSMessageSender smsMessageSender) {
    this.smsMessageSender = smsMessageSender;
  }

  @Override
  public void run(Response<OperationResponse> handlerResponse, GenerateOtpForNewLoanCommand command) {
    OperationResponse response = handlerResponse.getContent();
    String message = response.getMessage();
    smsMessageSender
      .sendMessage((new OtpMessageToSend(OtpUtils.getPhonePrefix(response.getPrefix(), response.getPhone()), message)));
  }
}
