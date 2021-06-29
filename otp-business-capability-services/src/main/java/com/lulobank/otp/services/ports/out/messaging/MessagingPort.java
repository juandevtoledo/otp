package com.lulobank.otp.services.ports.out.messaging;

public interface MessagingPort<T> {
    void sendMessage(T payload);
}
