package com.lulobank.otp.sdk.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;
import com.lulobank.otp.sdk.dto.external.ExternalOperationResponse;
import com.lulobank.otp.sdk.operations.exceptions.VerifyHireCreditException;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpCreditOperation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitOtpCreditOperationTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testOtpGeneration() throws JsonProcessingException {
        ExternalOperationResponse response = new ExternalOperationResponse("123123", 300L);

        ObjectMapper objectMapper = new ObjectMapper();
        RetrofitOtpCreditOperation retrofitOtpCreditOperation = new RetrofitOtpCreditOperation(
                "http://localhost:8981");
        wireMockRule.stubFor(post(urlMatching("/otp/operations/123/hire-loan/verify")).willReturn(
                aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(response))
                        .withHeader("Content-Type", "application/json")));
        Map<String, String> headers = new HashMap<>();
        assertNotNull(retrofitOtpCreditOperation.verifyHireCreditOperation(headers, buildWithdrawalOperation(), "123"));
    }


    @Test
    public void testOtpGeneration_Error_Generating_OTP() {
        RetrofitOtpCreditOperation retrofitOtpCreditOperation = new RetrofitOtpCreditOperation(
                "http://localhost:8981");
        wireMockRule.stubFor(post(urlMatching("/otp/operations/123/hire-loan/verify")).willReturn(
                aResponse().withStatus(500).withBody("").withHeader("Content-Type", "application/json")));
        exceptionRule.expect(VerifyHireCreditException.class);
        exceptionRule.expectMessage("");
        Map<String, String> headers = new HashMap<>();
        retrofitOtpCreditOperation.verifyHireCreditOperation(headers, buildWithdrawalOperation(), "123");
    }

    private ValidateOtpForNewLoan buildWithdrawalOperation() {
        ValidateOtpForNewLoan validateOtpForNewLoan = new ValidateOtpForNewLoan();
        validateOtpForNewLoan.setIdCredit("ABC");
        validateOtpForNewLoan.setIdOffer("456DEF");

        return validateOtpForNewLoan;
    }


}
