package com.lulobank.otp.starter.v3.adapters.out.redis;

import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import com.lulobank.otp.starter.v3.adapters.BaseUnitTest;
import com.lulobank.otp.starter.v3.adapters.Sample;
import io.vavr.control.Either;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static com.lulobank.otp.starter.v3.adapters.Constant.HASH_KEY;
import static com.lulobank.otp.starter.v3.adapters.Constant.OTP;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class HashRepositoryAdapterTest extends BaseUnitTest {
    @InjectMocks
    private HashRepositoryAdapter testedClass;

    @Test
    public void shouldReturnHashKeyNotFoundOnGetOTPByHashKey() {
        KeyValRepositoryError expectedError = KeyValRepositoryError.hasKeyNotFound();
        when(hashRedisRepository.findById(anyString())).thenReturn(Optional.empty());

        Either<KeyValRepositoryError, String> response = testedClass.findOTPById(HASH_KEY);

        assertTrue(response.isLeft());

        KeyValRepositoryError actualError = response.getLeft();

        assertEquals(expectedError.getProviderCode(), actualError.getProviderCode());
        assertEquals(expectedError.getBusinessCode(), actualError.getBusinessCode());
        assertEquals(expectedError.getDetail(), actualError.getDetail());
    }

    @Test
    public void shouldReturnConnectionErrorOnGetOTPByHashKey() {
        KeyValRepositoryError expectedError = KeyValRepositoryError.connectionError();
        when(hashRedisRepository.findById(anyString())).thenThrow(RuntimeException.class);

        Either<KeyValRepositoryError, String> response = testedClass.findOTPById(HASH_KEY);

        assertTrue(response.isLeft());

        KeyValRepositoryError actualError = response.getLeft();

        assertEquals(expectedError.getProviderCode(), actualError.getProviderCode());
        assertEquals(expectedError.getBusinessCode(), actualError.getBusinessCode());
        assertEquals(expectedError.getDetail(), actualError.getDetail());
    }

    @Test
    public void shouldReturnOTPOnGetOTPByHashKey() {
        when(hashRedisRepository.findById(anyString())).thenReturn(Optional.of(Sample.buildOtpRedisEntity()));

        Either<KeyValRepositoryError, String> response = testedClass.findOTPById(HASH_KEY);

        assertTrue(response.isRight());

        assertEquals(OTP, response.get());
    }

    @Test
    public void shouldReturnConnectionErrorOnRemoveOtp() {
        KeyValRepositoryError expectedError = KeyValRepositoryError.connectionError();
        doThrow(RuntimeException.class).when(hashRedisRepository).deleteById(anyString());

        Either<KeyValRepositoryError, Boolean> response = testedClass.deleteOTPById(HASH_KEY);

        assertTrue(response.isLeft());

        KeyValRepositoryError actualError = response.getLeft();

        assertEquals(expectedError.getProviderCode(), actualError.getProviderCode());
        assertEquals(expectedError.getBusinessCode(), actualError.getBusinessCode());
        assertEquals(expectedError.getDetail(), actualError.getDetail());
    }

    @Test
    public void shouldReturnOTPOnRemoveOtp() {
        Either<KeyValRepositoryError, Boolean> response = testedClass.deleteOTPById(HASH_KEY);

        assertTrue(response.isRight());

        assertEquals(TRUE, response.get());
    }

}