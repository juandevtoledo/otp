package com.lulobank.otp.starter.v3.adapters.out.clients;

import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
public class ClientsServiceClient {
    public static final String BASE_PATH = "clients";
    public static final String CLIENT_BY_ID_CARD_INTERNAL = "/idCardInternal?idCard={idCard}";
    public static final String ID_CARD_PLACEHOLDER = "{idCard}";

    private WebClient webClient;

    public ClientsServiceClient(String baseUrl) {
        webClient = WebClient.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .build();
    }
}

