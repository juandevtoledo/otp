package com.lulobank.otp.services.v3.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import com.lulobank.otp.services.v3.port.out.notifactions.NotifyService;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.services.v3.services.DefaultKeyService;
import com.lulobank.otp.services.v3.services.OTPGenerator;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;

import static com.lulobank.otp.services.v3.domain.OTPTransaction.TRANSFER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class GenerateOTPUseCaseTest {

    private GenerateOTPUseCase generateOTPUseCase;

    private KeyValRepository keyValRepository;

    private final ArgumentCaptor<String> argumentString = ArgumentCaptor.forClass(String.class);

    private final ArgumentCaptor<OTPTransaction> argumentOtpTransaction = ArgumentCaptor.forClass(OTPTransaction.class);


    @Before
    public void setUp() throws Exception {
        OTPGenerator otpGenerator = new OTPGenerator();
        keyValRepository = mock(KeyValRepository.class);
        NotifyService notifyService = mock(NotifyService.class);
        generateOTPUseCase = new GenerateOTPUseCase(
                new DefaultKeyService(),
                keyValRepository, notifyService, otpGenerator);
    }

    @Test
    public void generateOTPWhenRequestIsValid() throws IOException {
        OTPValidationRq rq = getOtpValidationRq("client-id-key");
        when(keyValRepository.save(anyString(), anyString(), Mockito.any()))
                .thenReturn(Try.of(() -> "example-save"));
        Try<String> execute = generateOTPUseCase.execute(rq);
        verify(keyValRepository).save(argumentString.capture(), anyString(), argumentOtpTransaction.capture());
        assertThat(argumentString.getValue(), is(equalTo("1111439607--1156528401--1871286875")));

        assertThat(execute.get(), is(equalTo("example-save")));
    }

    @Test
    public void generateOTPWhenRequestIsValidAndIdClientIsDifferent() throws IOException {
        OTPValidationRq rq = getOtpValidationRq("client-id-key-2");
        when(keyValRepository.save(anyString(), anyString(), Mockito.any()))
                .thenReturn(Try.of(() -> "example-save"));
        Try<String> execute = generateOTPUseCase.execute(rq);
        verify(keyValRepository).save(argumentString.capture(), anyString(), argumentOtpTransaction.capture());
        assertThat(argumentString.getValue(), is(not("1111439607--1156528401--1871286875")));

        assertThat(execute.get(), is(equalTo("example-save")));
    }

    @NotNull
    private OTPValidationRq getOtpValidationRq(String clientId) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"ddddd\",\"amount\":30000.0}");
        OTPValidationRq rq = new OTPValidationRq(transferJson, clientId, TRANSFER);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Headers Hell", "example of headers hell");
        rq.setHttpHeaders(headers);
        return rq;
    }

    @Test
    public void generateOTPWhenRedisFail() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"ddddd\",\"amount\":30000.0}");
        OTPValidationRq rq = new OTPValidationRq(transferJson, "client-id-key", TRANSFER);
        when(keyValRepository.save(anyString(), anyString(), Mockito.any()))
                .thenReturn(Try.failure(new RuntimeException("Redis is unavailable")));
        Try<String> execute = generateOTPUseCase.execute(rq);
        assertThat(execute.isFailure(), is(true));

    }

    @Test
    public void generateOTPWhenRequestIsNotValid() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"t\":\"ddddd\",\"a\":30000.0}");
        OTPValidationRq rq = new OTPValidationRq(transferJson, "client-id-key", TRANSFER);
        when(keyValRepository.get(anyString())).thenReturn(Option.none());
        Try<String> execute = generateOTPUseCase.execute(rq);
        assertThat(execute.isFailure(), is(true));

    }
}