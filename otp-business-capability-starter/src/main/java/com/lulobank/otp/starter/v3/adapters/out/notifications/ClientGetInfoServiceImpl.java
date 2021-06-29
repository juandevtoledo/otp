package com.lulobank.otp.starter.v3.adapters.out.notifications;

import com.lulobank.clients.sdk.operations.IClientInformationOperations;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.port.out.notifactions.ClientGetInfoService;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ClientGetInfoServiceImpl implements ClientGetInfoService {

    private IClientInformationOperations clientInformationOperations;

    public ClientGetInfoServiceImpl(IClientInformationOperations clientInformationOperations) {
        this.clientInformationOperations = clientInformationOperations;
    }

    @Override
    public Either<Throwable, ClientNotifyInfo> get(Map<String, String> headers, String clientId) {
        return Try.of(() -> clientInformationOperations.getAllClientInformationByIdClient(headers, clientId))
                .map(client -> {
                    log.info("client {}", client);
                    return new ClientNotifyInfo(client.getEmailAddress(), client.getPhoneNumber(), client.getPhonePrefix());
                })
                .onFailure(th -> log.error("Error trying to get client information ", th))
                .toEither();
    }
}
