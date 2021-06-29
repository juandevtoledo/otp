package com.lulobank.otp.sdk.operations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpOperationsOAuth2;
import com.lulobank.utils.exception.ServiceException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitOtpOperationsOAuth2Test {
  private static final String ERROR_MESSAGE_GENERATE_OTP = "Error OTP service GenerateOtp";
  private static final String ERROR_MESSAGE_VALIDATE_OTP = "Error OTP service ValidateOtp";
  private static final String ERROR_MESSAGE_VALIDATE_EMAIL_OTP = "Error OTP service ValidateEmailOtp";

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8981);
  @Rule
  public WireMockRule wireMockRuleAuthServer = new WireMockRule(9999);
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Before
  public void setUp() throws Exception {
    CognitoResponse cognitoResponse = new CognitoResponse("eyJraWQiOiJFc1BWR2pEU0FKNkJGRGFHVjArRlZ2aEtWU0ZPMVdtRUNTZnBKV2ZJWnFVPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJicHUyZ3JocG9mam5qZDljc2k3Z204YTV2IiwidG9rZW5fdXNlIjoiYWNjZXNzIiwic2NvcGUiOiJpbnRlcm5hbF9hcGlcL2ludGVybmFsX3RyYW5zYWN0aW9ucyIsImF1dGhfdGltZSI6MTU3OTE5MjMzNiwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfeFhhNVExeEhvIiwiZXhwIjoxNTc5MTk1OTM2LCJpYXQiOjE1NzkxOTIzMzYsInZlcnNpb24iOjIsImp0aSI6IjBlNDk4NzkxLWZkMDMtNDAxMC04NTY1LWU1OGE4NzYyM2U1ZSIsImNsaWVudF9pZCI6ImJwdTJncmhwb2ZqbmpkOWNzaTdnbThhNXYifQ.QztDEtnJRanOCoOyyOKopnXtt-paA_Cj7s6fMLOT_6DCQew9bnt0FFdo35nRrXTpumI-RPT23_YLoWXkvG4lSF2snGkSkZvqrpeOC6pVoU83GTy_fSDQ6Ks5G5S8BEBp8ccc2E_3dWJo-WeQTWV4Jsqp2SM61GNulTomRATsn1eUiMt03f1yTQ27b_n2MClceaXOf3GVs9YZoenWgN587su5LXrItTu-2Mv2evGLikRxWnFLG-yg6juidF0A2SAdjwqZbBdllEaUn5D9BJ1Vl5b2w7NcFE6T7kW9hKlbhc_6RP_31mFY_OainK0egdT-BB_lqZVrvblDpRvxMWYq8Q", 3600, "Bearer");
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    wireMockRuleAuthServer.stubFor(post(urlMatching("/token")).willReturn(
      aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(cognitoResponse))
        .withHeader("Content-Type", "application/json")));
  }

  @Test
  public void testOtpGeneration() throws JsonProcessingException {
    OtpResponse otpResponse = new OtpResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(post(urlMatching("/otp/generate/country/57/phonenumber/3162212121")).willReturn(
      aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(otpResponse))
        .withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertTrue(retrofitOtpOperationsOAuth2.generateOtp(headers, "57", "3162212121"));
  }

  @Test
  public void testOtpGeneration_Error_Generating_OTP() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(post(urlMatching("/otp/generate/country/57/phonenumber/3162212121")).willReturn(
      aResponse().withStatus(500).withBody("").withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_GENERATE_OTP);
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperationsOAuth2.generateOtp(headers, "57", "3162212121");
  }

  @Test
  public void testOtpValidation() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(get(urlMatching("/otp/verify/country/57/phonenumber/3162212121/token/1111")).willReturn(
      aResponse().withStatus(200).withBody("").withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertTrue(retrofitOtpOperationsOAuth2.validateOtp(headers, "57", "3162212121", "1111"));
  }

  @Test
  public void testOtpValidation_OTP_Invalid() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(get(urlMatching("/otp/verify/country/57/phonenumber/3162212121/token/1111")).willReturn(
      aResponse().withStatus(403).withBody("").withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperationsOAuth2.validateOtp(headers, "57", "3162212121", "1111");
  }

  @Test
  public void generateOtp_Error() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(post(urlMatching("/otp/generate/country/57/phonenumber/3162212121"))
      .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_GENERATE_OTP);
    ;
    Map<String, String> headers = new HashMap<>();
    //TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitOtpOperationsOAuth2.generateOtp(headers, "57", "3162212121");
  }

  @Test
  public void validateOtp_error() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(get(urlMatching("/otp/verify/country/57/phonenumber/3162212121/token/1111"))
      .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_VALIDATE_OTP);
    ;
    Map<String, String> headers = new HashMap<>();
    //TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitOtpOperationsOAuth2.validateOtp(headers, "57", "3162212121", "1111");
  }

  @Test
  public void testEmailOtpGeneration() throws JsonProcessingException {
    OtpResponse otpResponse = new OtpResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(post(urlMatching("/otp/generate/email/mail@mail.com")).willReturn(
      aResponse().withStatus(HttpStatus.OK.value()).withBody(objectMapper.writeValueAsString(otpResponse))
        .withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertTrue(retrofitOtpOperationsOAuth2.generateEmailOtp(headers, "mail@mail.com"));
  }

  @Test
  public void testOtpEmailGeneration_Error_Generating_OTP() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(post(urlMatching("/otp/generate/email/mail@mail.com")).willReturn(
      aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).withBody("")
        .withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperationsOAuth2.generateEmailOtp(headers, "mail@mail.com");
  }

  @Test
  public void testOtpEmailValidation() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(get(urlMatching("/otp/verify/email/mail@mail.com/token/1111")).willReturn(
      aResponse().withStatus(HttpStatus.OK.value()).withBody("").withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertTrue(retrofitOtpOperationsOAuth2.validateEmailOtp(headers, "mail@mail.com", "1111"));
  }

  @Test
  public void testOtpEmailValidation_OTP_Invalid() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(get(urlMatching("/otp/verify/email/mail@mail.com/token/1111")).willReturn(
      aResponse().withStatus(HttpStatus.NOT_ACCEPTABLE.value()).withBody("")
        .withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertFalse(retrofitOtpOperationsOAuth2.validateEmailOtp(headers, "mail@mail.com", "1111"));
  }

  @Test
  public void validateOtpEmail_error() {
    RetrofitOtpOperationsOAuth2 retrofitOtpOperationsOAuth2 = new RetrofitOtpOperationsOAuth2("http://localhost:8981", "cliendId", "clientSecret", "http://localhost:9999/", new TokenManager());
    wireMockRule.stubFor(get(urlMatching("/otp/verify/email/mail@mail.com/token/1111"))
      .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_VALIDATE_EMAIL_OTP);
    ;
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperationsOAuth2.validateEmailOtp(headers, "mail@mail.com", "1111");
  }
}
