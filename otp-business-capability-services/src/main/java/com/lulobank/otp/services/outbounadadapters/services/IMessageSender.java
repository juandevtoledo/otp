package com.lulobank.otp.services.outbounadadapters.services;

public interface IMessageSender {
  void sendMessage(IMessageToSend messageToSend);
}
