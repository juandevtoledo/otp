package com.lulobank.otp.starter.v3.adapters.out.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.http.Fault;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceError;
import com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceErrorStatus;
import com.lulobank.otp.starter.v3.adapters.BaseUnitTest;
import com.lulobank.otp.starter.v3.adapters.Sample;
import com.lulobank.otp.starter.v3.adapters.out.clients.dto.ResponseClientInformationByIdCard;
import io.vavr.control.Either;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.lulobank.otp.starter.v3.adapters.Constant.ID_CARD;
import static com.lulobank.otp.starter.v3.adapters.Constant.MAIL;
import static com.lulobank.otp.starter.v3.adapters.Constant.PHONE_NUMBER;
import static com.lulobank.otp.starter.v3.adapters.Constant.PHONE_PREFIX;
import static com.lulobank.otp.starter.v3.adapters.out.clients.ClientsServiceClient.BASE_PATH;
import static com.lulobank.otp.starter.v3.adapters.out.clients.ClientsServiceClient.CLIENT_BY_ID_CARD_INTERNAL;
import static com.lulobank.otp.starter.v3.adapters.out.clients.ClientsServiceClient.ID_CARD_PLACEHOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientsServiceAdapterTest extends BaseUnitTest {

    private ClientsServiceAdapter testedClass;

    @Before
    public void init() {
        testedClass = new ClientsServiceAdapter(new ClientsServiceClient("http://localhost:8082"));
    }

    @Test
    public void shouldReturnClientNotificationInfoByIdCard() throws JsonProcessingException {
        ResponseClientInformationByIdCard responseClientInformationByIdCard = Sample.getResponseClientInformationByIdCard();
        String responseStr = objectMapper.writeValueAsString(responseClientInformationByIdCard);
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CLIENT_BY_ID_CARD_INTERNAL.replace(ID_CARD_PLACEHOLDER, ID_CARD)))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(responseStr)));
        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<ClientsServiceError, ClientNotifyInfo> response = testedClass.getClientNotificationInfoSac(headers, ID_CARD);

        assertTrue(response.isRight());
        assertEquals(MAIL, response.get().getEmail());
        assertEquals(PHONE_NUMBER, response.get().getPhone());
        assertEquals(PHONE_PREFIX, response.get().getPrefix().intValue());

    }

    @Test
    public void shouldReturnErrorConnectionWhenGetClientNotificationInfoByIdCard() {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CLIENT_BY_ID_CARD_INTERNAL.replace(ID_CARD_PLACEHOLDER, ID_CARD)))
                        .willReturn(aResponse()
                                .withStatus(500)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<ClientsServiceError, ClientNotifyInfo> response = testedClass.getClientNotificationInfoSac(headers, ID_CARD);

        assertTrue(response.isLeft());
        assertEquals(ClientsServiceErrorStatus.OTP_110.name(), response.getLeft().getBusinessCode());

    }

    @Test
    public void shouldReturnNotFoundWhenGetClientNotificationInfoByIdCard() {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CLIENT_BY_ID_CARD_INTERNAL.replace(ID_CARD_PLACEHOLDER, ID_CARD)))
                        .willReturn(aResponse()
                                .withStatus(404)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<ClientsServiceError, ClientNotifyInfo> response = testedClass.getClientNotificationInfoSac(headers, ID_CARD);

        assertTrue(response.isLeft());
        assertEquals(ClientsServiceErrorStatus.OTP_111.name(), response.getLeft().getBusinessCode());

    }

    @Test
    public void shouldReturnErrorSinceWhenGetClientNotificationInfoByIdCardWhenConnectionException() {
        wireMockRule.stubFor(
                get(urlEqualTo("/" + BASE_PATH + CLIENT_BY_ID_CARD_INTERNAL.replace(ID_CARD_PLACEHOLDER, ID_CARD)))
                        .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        Map<String, String> headers = Sample.getAuthorizationHeader();
        Either<ClientsServiceError, ClientNotifyInfo> response = testedClass.getClientNotificationInfoSac(headers, ID_CARD);

        assertTrue(response.isLeft());
        assertEquals(ClientsServiceErrorStatus.OTP_110.name(), response.getLeft().getBusinessCode());

    }


}
