package com.lulobank.otp.services.v3.usecase.deleteotptoken;

import com.lulobank.otp.services.v3.BaseUnitTest;
import com.lulobank.otp.services.v3.Sample;
import com.lulobank.otp.services.v3.domain.deleteotptoken.DeleteOTPCardlessWithdrawalRequest;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import com.lulobank.otp.services.v3.util.HttpDomainStatus;
import io.vavr.control.Either;
import org.junit.Test;
import org.mockito.InjectMocks;

import static com.lulobank.otp.services.Constants.OTP_NUMBER;
import static com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus.OTP_184;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteOTPCardlessWithdrawalUseCaseTest extends BaseUnitTest {

    @InjectMocks
    private DeleteOTPCardlessWithdrawalUseCase testedClass;

    @Test
    public void shouldReturnHashKeyNotFound() {
        DeleteOTPCardlessWithdrawalRequest deleteOTPCardlessWithdrawalRequest = Sample.buildDeleteOTPCardlessWithdrawalRequest();
        KeyValRepositoryError expectedError = KeyValRepositoryError.hasKeyNotFound();
        when(hashRepositoryPort.findOTPById(anyString())).thenReturn(Either.left(expectedError));

        Either<UseCaseResponseError, Boolean> response = testedClass.execute(deleteOTPCardlessWithdrawalRequest);

        assertTrue(response.isLeft());

        UseCaseResponseError actualError = response.getLeft();

        assertEquals(expectedError.getBusinessCode(), actualError.getBusinessCode());
        assertEquals(expectedError.getDetail(), actualError.getDetail());
        assertEquals(expectedError.getProviderCode(), actualError.getProviderCode());

        verify(hashRepositoryPort).findOTPById(anyString());
        verify(hashRepositoryPort, times(0)).deleteOTPById(anyString());
    }

    @Test
    public void shouldReturnErrorRemovingOTP() {
        DeleteOTPCardlessWithdrawalRequest deleteOTPCardlessWithdrawalRequest = Sample.buildDeleteOTPCardlessWithdrawalRequest();
        KeyValRepositoryError expectedError = KeyValRepositoryError.connectionError();
        when(hashRepositoryPort.findOTPById(anyString())).thenReturn(Either.right(OTP_NUMBER));
        when(hashRepositoryPort.deleteOTPById(anyString())).thenReturn(Either.left(expectedError));

        Either<UseCaseResponseError, Boolean> response = testedClass.execute(deleteOTPCardlessWithdrawalRequest);

        assertTrue(response.isLeft());

        UseCaseResponseError actualError = response.getLeft();

        assertEquals(expectedError.getBusinessCode(), actualError.getBusinessCode());
        assertEquals(expectedError.getDetail(), actualError.getDetail());
        assertEquals(expectedError.getProviderCode(), actualError.getProviderCode());

        verify(hashRepositoryPort).findOTPById(anyString());
        verify(hashRepositoryPort).deleteOTPById(anyString());
    }

    @Test
    public void shouldReturnOTPDoesNotMatch() {
        DeleteOTPCardlessWithdrawalRequest deleteOTPCardlessWithdrawalRequest = Sample.buildDeleteOTPCardlessWithdrawalRequest();
        when(hashRepositoryPort.findOTPById(anyString())).thenReturn(Either.right("987654"));

        Either<UseCaseResponseError, Boolean> response = testedClass.execute(deleteOTPCardlessWithdrawalRequest);

        assertTrue(response.isLeft());

        UseCaseResponseError actualError = response.getLeft();

        assertEquals(OTP_184.name(), actualError.getBusinessCode());
        assertEquals("U", actualError.getDetail());
        assertEquals(String.valueOf(HttpDomainStatus.PRECONDITION_FAILED.value()), actualError.getProviderCode());

        verify(hashRepositoryPort).findOTPById(anyString());
        verify(hashRepositoryPort, times(0)).deleteOTPById(anyString());
    }

    @Test
    public void shouldReturnTrueOnRemovingOTP() {
        DeleteOTPCardlessWithdrawalRequest deleteOTPCardlessWithdrawalRequest = Sample.buildDeleteOTPCardlessWithdrawalRequest();
        when(hashRepositoryPort.findOTPById(anyString())).thenReturn(Either.right(OTP_NUMBER));
        when(hashRepositoryPort.deleteOTPById(anyString())).thenReturn(Either.right(Boolean.TRUE));

        Either<UseCaseResponseError, Boolean> response = testedClass.execute(deleteOTPCardlessWithdrawalRequest);

        assertTrue(response.isRight());

        assertTrue(response.get());

        verify(hashRepositoryPort).findOTPById(anyString());
        verify(hashRepositoryPort).deleteOTPById(anyString());
    }
}