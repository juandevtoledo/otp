package com.lulobank.otp.services.v3.usecase;

import com.lulobank.otp.services.v3.BaseUnitTest;
import com.lulobank.otp.services.v3.Sample;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.domain.zendesk.AuthorizationSac;
import com.lulobank.otp.services.v3.domain.zendesk.TransactionTypeSac;
import com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceError;
import com.lulobank.otp.services.v3.port.out.clients.error.ClientsServiceErrorStatus;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus;
import com.lulobank.otp.services.v3.usecase.sac.SacGenerateAuthorizationUseCase;
import io.vavr.control.Either;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;

import static com.lulobank.otp.services.Constants.ID_CARD;
import static com.lulobank.otp.services.Constants.OTP_NUMBER;
import static com.lulobank.otp.services.v3.domain.zendesk.TransactionTypeSac.FREEZE_CARD_SAC;
import static com.lulobank.otp.services.v3.domain.zendesk.TransactionTypeSac.LOCK_ACCOUNT_SAC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SacGenerateAuthorizationUseCaseTest extends BaseUnitTest {

    @InjectMocks
    private SacGenerateAuthorizationUseCase testedClass;

    @Captor
    ArgumentCaptor<String> keyCaptor;
    @Captor
    ArgumentCaptor<String> orpCaptor;
    @Captor
    ArgumentCaptor<String> messageCaptor;
    @Captor
    ArgumentCaptor<ClientNotifyInfo> clientNotifyInfoArgumentCaptor;

    @Test
    public void shouldReturnOk() {
        ClientNotifyInfo clientNotifyInfo = Sample.getClientNotifyInfo();
        when(clientsPort.getClientNotificationInfoSac(headersCaptor.capture(), idCardCaptor.capture()))
                .thenReturn(Either.right(clientNotifyInfo));
        when(keyValRepository.save(keyCaptor.capture(), any())).thenReturn(Either.right(OTP_NUMBER));
        doNothing().when(notifyService).notifyAuthorizationSac(orpCaptor.capture(), clientNotifyInfoArgumentCaptor.capture(),
                messageCaptor.capture());

        AuthorizationSac request = Sample.getAuthorizationSac();
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(notifyService, timeout(100)).notifyAuthorizationSac(any(),any(),any());

        assertTrue(response.isRight());
        assertTrue(response.get());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getValue());
        assertEquals(ID_CARD, idCardCaptor.getValue());
        assertEquals(buildKey(request), keyCaptor.getValue());
        assertEquals(OTP_NUMBER, orpCaptor.getValue());
        assertEquals(clientNotifyInfo.getEmail(), clientNotifyInfoArgumentCaptor.getValue().getEmail());
        assertEquals(clientNotifyInfo.getPhone(), clientNotifyInfoArgumentCaptor.getValue().getPhone());
        assertEquals(clientNotifyInfo.getPrefix(), clientNotifyInfoArgumentCaptor.getValue().getPrefix());
        assertEquals(LOCK_ACCOUNT_SAC.name(), messageCaptor.getValue());

    }

    @Test
    public void shouldReturnOkFreezeCard() {
        ClientNotifyInfo clientNotifyInfo = Sample.getClientNotifyInfo();
        when(clientsPort.getClientNotificationInfoSac(headersCaptor.capture(), idCardCaptor.capture()))
                .thenReturn(Either.right(clientNotifyInfo));
        when(keyValRepository.save(keyCaptor.capture(), any())).thenReturn(Either.right(OTP_NUMBER));
        doNothing().when(notifyService).notifyAuthorizationSac(orpCaptor.capture(), clientNotifyInfoArgumentCaptor.capture(),
                messageCaptor.capture());

        AuthorizationSac request = Sample.getAuthorizationSac();
        request.setTransactionType(TransactionTypeSac.FREEZE_CARD_SAC.name());
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(notifyService, timeout(100)).notifyAuthorizationSac(any(),any(),any());

        assertTrue(response.isRight());
        assertTrue(response.get());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getValue());
        assertEquals(ID_CARD, idCardCaptor.getValue());
        assertEquals(buildKey(request), keyCaptor.getValue());
        assertEquals(OTP_NUMBER, orpCaptor.getValue());
        assertEquals(clientNotifyInfo.getEmail(), clientNotifyInfoArgumentCaptor.getValue().getEmail());
        assertEquals(clientNotifyInfo.getPhone(), clientNotifyInfoArgumentCaptor.getValue().getPhone());
        assertEquals(clientNotifyInfo.getPrefix(), clientNotifyInfoArgumentCaptor.getValue().getPrefix());
        assertEquals(FREEZE_CARD_SAC.name(), messageCaptor.getValue());

    }

    @Test
    public void shouldReturnFailClientNotFound() {
        when(clientsPort.getClientNotificationInfoSac(headersCaptor.capture(), idCardCaptor.capture()))
                .thenReturn(Either.left(ClientsServiceError.accountNotFound()));

        AuthorizationSac request = Sample.getAuthorizationSac();
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(notifyService, times(0)).notifyAuthorizationSac(any(),any(),any());


        assertTrue(response.isLeft());
        assertEquals(ClientsServiceErrorStatus.OTP_111.name(),response.getLeft().getBusinessCode());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getValue());
        assertEquals(ID_CARD, idCardCaptor.getValue());

    }

    @Test
    public void shouldReturnFailClientServiceError() {
        when(clientsPort.getClientNotificationInfoSac(headersCaptor.capture(), idCardCaptor.capture()))
                .thenReturn(Either.left(ClientsServiceError.connectionError()));

        AuthorizationSac request = Sample.getAuthorizationSac();
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(notifyService, times(0)).notifyAuthorizationSac(any(),any(),any());

        assertTrue(response.isLeft());
        assertEquals(ClientsServiceErrorStatus.OTP_110.name(),response.getLeft().getBusinessCode());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getValue());
        assertEquals(ID_CARD, idCardCaptor.getValue());

    }

    @Test
    public void shouldReturnFailKeyValError() {
        ClientNotifyInfo clientNotifyInfo = Sample.getClientNotifyInfo();
        when(clientsPort.getClientNotificationInfoSac(headersCaptor.capture(), idCardCaptor.capture()))
                .thenReturn(Either.right(clientNotifyInfo));
        when(keyValRepository.save(keyCaptor.capture(), any()))
                .thenReturn(Either.left(KeyValRepositoryError.connectionError()));

        AuthorizationSac request = Sample.getAuthorizationSac();
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(notifyService, times(0)).notifyAuthorizationSac(any(),any(),any());

        assertTrue(response.isLeft());
        assertEquals(KeyValRepositoryErrorStatus.OTP_115.name(),response.getLeft().getBusinessCode());
        assertEquals(request.getAuthorizationHeader(), headersCaptor.getValue());
        assertEquals(ID_CARD, idCardCaptor.getValue());
        assertEquals(buildKey(request), keyCaptor.getValue());

    }

    @Test
    public void shouldReturnFailInvalidTransactionType() {

        AuthorizationSac request = Sample.getAuthorizationSac();
        request.setTransactionType("abc");
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(clientsPort, times(0)).getClientNotificationInfoSac(any(),any());
        verify(keyValRepository, times(0)).save(any(),any(),any());
        verify(notifyService, times(0)).notifyAuthorizationSac(any(),any(),any());

        assertTrue(response.isLeft());
        assertEquals(UseCaseErrorStatus.OTP_181.name(),response.getLeft().getBusinessCode());

    }

    private String buildKey(AuthorizationSac authorizationSac) {
        return authorizationSac.getClientId() + ":" +
                authorizationSac.getAgentId() + ":" +
                authorizationSac.getTransactionType() + ":" +
                authorizationSac.getProductNumber();
    }
}
