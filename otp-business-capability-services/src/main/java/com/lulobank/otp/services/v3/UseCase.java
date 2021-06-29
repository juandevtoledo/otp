package com.lulobank.otp.services.v3;

import io.vavr.control.Try;

@FunctionalInterface
public interface UseCase<T, R> {
    Try<R> execute(T command);
}