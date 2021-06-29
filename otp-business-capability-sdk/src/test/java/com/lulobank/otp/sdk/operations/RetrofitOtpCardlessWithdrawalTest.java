package com.lulobank.otp.sdk.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.otp.sdk.dto.external.ExternalOperationResponse;
import com.lulobank.otp.sdk.dto.external.Withdrawal;
import com.lulobank.otp.sdk.dto.external.WithdrawalOperation;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpCardlessWithdrawal;
import com.lulobank.utils.exception.ServiceException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitOtpCardlessWithdrawalTest {
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8981);
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void testOtpGeneration() throws JsonProcessingException {
    ExternalOperationResponse response = new ExternalOperationResponse("123123", 300L);

    ObjectMapper objectMapper = new ObjectMapper();
    RetrofitOtpCardlessWithdrawal retrofitOtpCardlessWithdrawalOperation = new RetrofitOtpCardlessWithdrawal(
      "http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/operations/withdrawal")).willReturn(
      aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(response))
        .withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertNotNull(retrofitOtpCardlessWithdrawalOperation.generateOtpCardlessWithdrawal(headers, buildWithdrawalOperation()));
  }

  @Test
  public void testOtpGeneration_Error_Generating_OTP() {
    RetrofitOtpCardlessWithdrawal retrofitOtpCardlessWithdrawalOperation = new RetrofitOtpCardlessWithdrawal(
      "http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/operations/withdrawal")).willReturn(
      aResponse().withStatus(500).withBody("").withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    Map<String, String> headers = new HashMap<>();
    retrofitOtpCardlessWithdrawalOperation.generateOtpCardlessWithdrawal(headers, buildWithdrawalOperation());
  }

  private WithdrawalOperation buildWithdrawalOperation() {
    WithdrawalOperation withdrawalOperation = new WithdrawalOperation();
    Withdrawal withdrawal = new Withdrawal();
    withdrawal.setAmount(BigInteger.valueOf(200000));
    withdrawal.setDocumentId("123456789");
    withdrawalOperation.setWithdrawal(withdrawal);

    return withdrawalOperation;
  }
}
