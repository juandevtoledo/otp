package com.lulobank.otp.services.v3.port.out.clients.error;


import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.util.HttpDomainStatus;

import static com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceErrorStatus.OTP_110;
import static com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceErrorStatus.OTP_111;

public class ClientsServiceError extends UseCaseResponseError {
    public ClientsServiceError(ClientsServiceErrorStatus clientsServiceErrorStatus, String providerCode) {
        super(clientsServiceErrorStatus.name(), providerCode, ClientsServiceErrorStatus.DEFAULT_DETAIL);
    }

    public static ClientsServiceError connectionError(String providerCode) {
        return new ClientsServiceError(OTP_110, providerCode);
    }

    public static ClientsServiceError connectionError() {
        return new ClientsServiceError(OTP_110, String.valueOf(HttpDomainStatus.BAD_GATEWAY.value()));
    }

    public static ClientsServiceError accountNotFound() {
        return new ClientsServiceError(OTP_111, String.valueOf(HttpDomainStatus.NOT_FOUND.value()));
    }
}
