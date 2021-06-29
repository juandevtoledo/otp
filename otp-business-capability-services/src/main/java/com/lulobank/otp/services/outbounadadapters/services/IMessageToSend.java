package com.lulobank.otp.services.outbounadadapters.services;

public interface IMessageToSend {
  String getPhone();

  String getMessage();
}
