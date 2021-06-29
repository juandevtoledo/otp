package com.lulobank.otp.services.v3.port.out.clients;

import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceError;
import io.vavr.control.Either;

import java.util.Map;

public interface ClientsPort {

    Either<ClientsServiceError, ClientNotifyInfo> getClientNotificationInfoSac(Map<String, String> headers, String idCard);

}
