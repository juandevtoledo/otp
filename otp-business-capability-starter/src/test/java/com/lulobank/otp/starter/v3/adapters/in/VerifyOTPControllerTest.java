package com.lulobank.otp.starter.v3.adapters.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.services.exceptions.OTPNotMatchException;
import com.lulobank.otp.services.v3.domain.OTPChannel;
import com.lulobank.otp.services.v3.domain.OTPGenerationRs;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.domain.VerifyOTPRs;
import com.lulobank.otp.services.v3.domain.transactions.Credit;
import com.lulobank.otp.services.v3.port.in.VerifyOTP;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class VerifyOTPControllerTest {

    private static final String AUTHZ_TOKEN = "adfasdfwefasdvsdvdafvadfsdf";
    private static final String ID_CLIENT = "234-2345-456-4567-67";
    private String base64 = "eyJ0YXJnZXQiOiJkZGRkZCIsImFtb3VudCI6MzAwMDAuMH0=";

    @Mock
    private VerifyOTP verifyOTP;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private VerifyOTPController verifyOTPController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(verifyOTPController);
    }


    @Test
    public void shouldReturn200OKWhenVerifyCredit() {
        HttpHeaders headers = mockExecute(AUTHZ_TOKEN);
        ResponseEntity<VerifyOTPRs> verify = verifyOTPController.verify(headers, ID_CLIENT, OTPTransaction.CREDIT,
                createJsonNode(), bindingResult);
        assertEquals(200, verify.getStatusCodeValue());

    }


    @Test
    public void shouldReturn422UnprocessableWhenVerifyCreditChecksum() {
        HttpHeaders headers = mockExecute(AUTHZ_TOKEN);
        ResponseEntity<VerifyOTPRs> verify = verifyOTPController.verifyChecksum(headers, ID_CLIENT, OTPTransaction.CREDIT);
        assertEquals(422, verify.getStatusCodeValue());
    }


    @Test
    public void shouldReturn200OKWhenVerifyCreditChecksum() {

        HttpHeaders headers = mockExecute("2456:" + base64);

        ResponseEntity<VerifyOTPRs> verify = verifyOTPController.verifyChecksum(headers, ID_CLIENT, OTPTransaction.CREDIT);

        assertEquals(200, verify.getStatusCodeValue());

    }

    @Test
    public void shouldReturn406WhenVerifyChecksum() throws NoSuchFieldException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("otp-token", "2456:"+base64);
        given(verifyOTP.execute(any())).willReturn(Try.failure(new OTPNotMatchException("")));
        ResponseEntity<VerifyOTPRs> verify = verifyOTPController.verifyChecksum(headers, ID_CLIENT, OTPTransaction.CREDIT);

        assertEquals(406, verify.getStatusCodeValue());

    }

    @Test
    public void shouldReturn406WhenMockVerifyTrue() throws NoSuchFieldException {
        FieldSetter.setField(verifyOTPController, verifyOTPController.getClass().getDeclaredField("mockVerify"), true);
        HttpHeaders headers = new HttpHeaders();
        headers.set("otp-token", "1403:"+base64);
        ResponseEntity<VerifyOTPRs> verify = verifyOTPController.verifyChecksum(headers, ID_CLIENT, OTPTransaction.CREDIT);
        assertEquals(406, verify.getStatusCodeValue());
    }

    private JsonNode createJsonNode() {
        return new ObjectMapper().convertValue(new Credit("", 0.0, "", 1), JsonNode.class);
    }

    @NotNull
    private HttpHeaders mockExecute(String header) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("otp-token", header);
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(verifyOTP.execute(any())).willReturn(Try.of(() -> new VerifyOTPRs(true)));
        return headers;
    }


}
