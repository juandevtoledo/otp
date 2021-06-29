package com.lulobank.otp.services.v3.util;

@FunctionalInterface
public interface UseCase<T, R> {
    R execute(T command);
}