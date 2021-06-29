package com.lulobank.otp.services.v3.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.services.exceptions.OTPNotFoundException;
import com.lulobank.otp.services.exceptions.OTPNotMatchException;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import com.lulobank.otp.services.v3.domain.VerifyOTPRs;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.services.v3.services.DefaultKeyService;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.matchers.InstanceOf;

import java.io.IOException;
import java.util.HashMap;

import static com.lulobank.otp.services.v3.domain.OTPTransaction.TRANSFER;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class VerifyOTPUseCaseTest {

    private VerifyOTPUseCase verifyOTPUseCase;

    private KeyValRepository keyValRepository;

    @Before
    public void setUp() {
        keyValRepository = mock(KeyValRepository.class);
        verifyOTPUseCase = new VerifyOTPUseCase(new DefaultKeyService(), keyValRepository);
    }

    @Test
    public void verifyOtpWhenOTPIsPresentInRedis() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"target\",\"amount\":30000.0}");
        OTPValidationRq rq = new OTPValidationRq(transferJson, "client-id-key", TRANSFER, "8888");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "example");
        rq.setHttpHeaders(headers);
        when(keyValRepository.get(anyString())).thenReturn(Option.of("8888"));
        when(keyValRepository.remove(anyString())).thenReturn(Try.success(true));
        Try<VerifyOTPRs> execute = verifyOTPUseCase.execute(rq);
        verify(keyValRepository, times(1)).remove(anyString());
        assertThat(execute.get().isValid(), is(true));
    }

    @Test
    public void verifyOtpWhenOTPIsPresentInRedisButOtpIsInvalid() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"target\",\"amount\":30000.0}");
        OTPValidationRq rq = new OTPValidationRq(transferJson, "client-id-key", TRANSFER, "7777");
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "example");
        rq.setHttpHeaders(headers);
        when(keyValRepository.get(anyString())).thenReturn(Option.of("6666"));
        Try<VerifyOTPRs> execute = verifyOTPUseCase.execute(rq);

        assertThat(execute.isFailure(), is(true));
        assertEquals(OTPNotMatchException.class, execute.getCause().getClass());
    }

    @Test
    public void verifyOtpWhenOTPIsNotPresentInRedis() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"target-zero\",\"amount\":30000.0}");
        OTPValidationRq rq = new OTPValidationRq(transferJson, "client-id-key", TRANSFER);
        when(keyValRepository.get(anyString())).thenReturn(Option.none());
        Try<VerifyOTPRs> execute = verifyOTPUseCase.execute(rq);
        assertThat(execute.isFailure(), is(true));
        assertEquals(OTPNotFoundException.class, execute.getCause().getClass() );
    }

    @Test
    public void verifyOtpWhenRequestIsNotValid() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"t\":\"target-zero\",\"a\":30000.0}");
        OTPValidationRq rq = new OTPValidationRq(transferJson, "client-id-key", TRANSFER);
        when(keyValRepository.get(anyString())).thenReturn(Option.none());
        when(keyValRepository.remove(anyString())).thenReturn(Try.success(true));
        Try<VerifyOTPRs> execute = verifyOTPUseCase.execute(rq);
        verify(keyValRepository, times(0)).remove(anyString());
        assertThat(execute.isFailure(), is(true));
    }
}