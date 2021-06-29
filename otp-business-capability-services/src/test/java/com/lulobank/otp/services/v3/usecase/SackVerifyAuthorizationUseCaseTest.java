package com.lulobank.otp.services.v3.usecase;

import com.lulobank.otp.services.v3.BaseUnitTest;
import com.lulobank.otp.services.v3.Sample;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.domain.zendesk.AuthorizationSac;
import com.lulobank.otp.services.v3.domain.zendesk.VerifyAuthorizationSac;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus;
import com.lulobank.otp.services.v3.usecase.sac.SackVerifyAuthorizationUseCase;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;

import static com.lulobank.otp.services.Constants.OTP_NUMBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SackVerifyAuthorizationUseCaseTest extends BaseUnitTest {

    @InjectMocks
    private SackVerifyAuthorizationUseCase testedClass;

    @Captor
    ArgumentCaptor<String> keyCaptor;

    @Test
    public void shouldReturnFailInvalidTransactionType() {

        VerifyAuthorizationSac request = Sample.getVerifyAuthorizationSac();
        request.setTransactionType("abc");
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(keyValRepository, times(0)).getByKey(any());
        verify(keyValRepository, times(0)).remove(any());

        assertTrue(response.isLeft());
        assertEquals(UseCaseErrorStatus.OTP_181.name(), response.getLeft().getBusinessCode());

    }

    @Test
    public void shouldReturnFailKeyValConnectionError() {

        when(keyValRepository.getByKey(keyCaptor.capture()))
                .thenReturn(Either.left(KeyValRepositoryError.connectionError()));

        VerifyAuthorizationSac request = Sample.getVerifyAuthorizationSac();
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(keyValRepository, times(1)).getByKey(any());
        verify(keyValRepository, times(0)).remove(any());

        assertTrue(response.isLeft());
        assertEquals(KeyValRepositoryErrorStatus.OTP_115.name(), response.getLeft().getBusinessCode());

    }

    @Test
    public void shouldReturnFailInvalidOtp() {

        when(keyValRepository.getByKey(keyCaptor.capture()))
                .thenReturn(Either.right(Option.of("1111")));

        VerifyAuthorizationSac request = Sample.getVerifyAuthorizationSac();
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(keyValRepository, times(1)).getByKey(any());
        verify(keyValRepository, times(0)).remove(any());

        assertTrue(response.isLeft());
        assertEquals(UseCaseErrorStatus.OTP_182.name(), response.getLeft().getBusinessCode());
        assertEquals(buildKey(request), keyCaptor.getValue());

    }

    @Test
    public void shouldReturnOk() {

        when(keyValRepository.getByKey(keyCaptor.capture()))
                .thenReturn(Either.right(Option.of(OTP_NUMBER)));

        VerifyAuthorizationSac request = Sample.getVerifyAuthorizationSac();
        Either<UseCaseResponseError, Boolean> response = testedClass.execute(request);

        verify(keyValRepository, times(1)).getByKey(any());
        verify(keyValRepository, times(1)).remove(any());

        assertTrue(response.isRight());
        assertTrue(response.get());
        assertEquals(buildKey(request), keyCaptor.getValue());

    }

    private String buildKey(VerifyAuthorizationSac verifyAuthorizationSac) {
        return verifyAuthorizationSac.getClientId() + ":" +
                verifyAuthorizationSac.getAgentId() + ":" +
                verifyAuthorizationSac.getTransactionType() + ":" +
                verifyAuthorizationSac.getProductNumber();
    }


}
