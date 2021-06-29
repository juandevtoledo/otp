package com.lulobank.otp.services.v3.port.out.notifactions;

import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import io.vavr.control.Either;

import java.util.Map;

public interface ClientGetInfoService {

    Either<Throwable, ClientNotifyInfo> get(Map<String, String> headers, String clientId);

}
