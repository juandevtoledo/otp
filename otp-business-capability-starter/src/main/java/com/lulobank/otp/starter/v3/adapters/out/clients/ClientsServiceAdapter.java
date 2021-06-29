package com.lulobank.otp.starter.v3.adapters.out.clients;

import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.port.out.clients.ClientsPort;
import com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceError;
import com.lulobank.otp.starter.v3.adapters.out.clients.dto.ResponseClientInformationByIdCard;
import com.lulobank.otp.starter.v3.adapters.out.clients.mapper.ClientsServiceMapper;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.Map;

import static com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceError.connectionError;
import static com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceErrorStatus.OTP_110;
import static com.lulobank.otp.starter.v3.adapters.out.clients.ClientsServiceClient.CLIENT_BY_ID_CARD_INTERNAL;
import static com.lulobank.otp.starter.v3.adapters.out.clients.ClientsServiceClient.ID_CARD_PLACEHOLDER;
import static com.lulobank.otp.starter.v3.adapters.out.util.WebClientUtil.mapToHttpHeaders;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.isIn;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
public class ClientsServiceAdapter implements ClientsPort {

    private final ClientsServiceClient clientsServiceClient;

    @Override
    public Either<ClientsServiceError, ClientNotifyInfo> getClientNotificationInfoSac(Map<String, String> headers, String idCard) {
        return Try.of(() -> getClientByIdCardInternal(headers,idCard))
                .map(clientResponse -> Option.of(clientResponse)
                        .filter(clientResponse1 -> !clientResponse1.statusCode().isError())
                        .map(clientResponse1 -> clientResponse.bodyToMono(ResponseClientInformationByIdCard.class)
                                .block())
                        .map(ResponseClientInformationByIdCard::getContent)
                        .map(ClientsServiceMapper.INSTANCE::toDomain)
                        .toEither(() -> getClientsServiceErrorFromClientResponse(clientResponse)))
                .recover(t -> Either.left(handleException(t)))
                .get();
    }

    private ClientResponse getClientByIdCardInternal(Map<String, String> headers, String idClient) {
        String path = CLIENT_BY_ID_CARD_INTERNAL.replace(ID_CARD_PLACEHOLDER, idClient);
        return clientsServiceClient.getWebClient()
                .get()
                .uri(path, Collections.emptyMap())
                .headers(httpHeaders -> mapToHttpHeaders(headers, httpHeaders))
                .exchange()
                .block();
    }

    private ClientsServiceError getClientsServiceErrorFromClientResponse(ClientResponse clientResponse) {
        return clientResponse.createException()
                .map(this::handleWebClientResponseException)
                .block();
    }

    private ClientsServiceError handleWebClientResponseException(WebClientResponseException e) {
        log.error("WebClientResponseException when getting savingsAccount by idClient: {} ", e.getResponseBodyAsString(), e);
        return API.Match(e.getStatusCode()).of(
                Case($(isIn(NOT_FOUND)), ClientsServiceError::accountNotFound),
                Case($(), () -> connectionError(String.valueOf(e.getStatusCode().value())))
        );
    }

    private ClientsServiceError handleException(Throwable t) {
        log.error(OTP_110.getMessage(), t);
        return connectionError();
    }

}
