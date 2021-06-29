package com.lulobank.otp.sdk.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.core.Response;
import com.lulobank.otp.sdk.dto.exceptions.AuthorizationIvrException;
import com.lulobank.otp.sdk.dto.exceptions.AuthorizationSacException;
import com.lulobank.otp.sdk.dto.exceptions.OtpValidationException;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrResponse;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrResponse;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.sdk.dto.zendesk.TransactionTypeSac;
import com.lulobank.otp.sdk.dto.zendesk.ValidateAuthorizationSacRequest;
import com.lulobank.otp.sdk.operations.impl.RetrofitOtpOperations;
import com.lulobank.utils.exception.ServiceException;
import joptsimple.internal.Strings;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitOtpOperationsTest {
  private static final String ERROR_MESSAGE_VALIDATE_AUTHORIZATION_SAC = "Error OTP service ValidateAuthorizationSac";
  private static final String ERROR_MESSAGE_GENERATE_OTP = "Error OTP service GenerateOtp";
  private static final String ERROR_MESSAGE_GENERATE_EMAIL_OTP = "Error OTP service GenerateEmailOtp";
  private static final String ERROR_MESSAGE_VALIDATE_OTP = "Error OTP service ValidateOtp";
  private static final String ERROR_MESSAGE_VALIDATE_EMAIL_OTP = "Error OTP service ValidateEmailOtp";
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8981);
  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  private static final String OTP = "456";
  private static final String TRANSACTION_TYPE = "ACCOUNT_LOCK_IVR";
  private static final String PRODUCT_NUMBER = "AAASSD33";
  private static final String DOCUMENT_NUMBER = "999999";

  private static final String URL_SERVICE_GENERATE_OTP =
          "/otp/ivrInternal";
  private static final String URL_SERVICE_VALIDATE_OTP =
          "/otp/verify/ivrInternal";
  private static final String CONTENT_TYPE = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";

  private static ObjectMapper objectMapper;

  private static AuthorizationIvrResponse authorizationIvrResponse;
  private static ValidateAuthorizationIvrResponse validateAuthorizationIvrResponse;


  @Test
  public void testOtpGeneration() throws JsonProcessingException {
    OtpResponse otpResponse = new OtpResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/generate/country/57/phonenumber/3162212121")).willReturn(
      aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(otpResponse))
        .withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertTrue(retrofitOtpOperations.generateOtp(headers, "57", "3162212121"));
  }

  @Test
  public void testOtpGeneration_Error_Generating_OTP() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/generate/country/57/phonenumber/3162212121")).willReturn(
      aResponse().withStatus(500).withBody("").withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_GENERATE_OTP);
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperations.generateOtp(headers, "57", "3162212121");
  }

  @Test
  public void testOtpValidation() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(get(urlMatching("/otp/verify/country/57/phonenumber/3162212121/token/1111")).willReturn(
      aResponse().withStatus(200).withBody("").withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertTrue(retrofitOtpOperations.validateOtp(headers, "57", "3162212121", "1111"));
  }

  @Test
  public void testOtpValidation_OTP_Invalid() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(get(urlMatching("/otp/verify/country/57/phonenumber/3162212121/token/1111")).willReturn(
      aResponse().withStatus(403).withBody("").withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperations.validateOtp(headers, "57", "3162212121", "1111");
  }

  @Test
  public void generateOtp_Error() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/generate/country/57/phonenumber/3162212121"))
      .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_GENERATE_OTP);
    ;
    Map<String, String> headers = new HashMap<>();
    //TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitOtpOperations.generateOtp(headers, "57", "3162212121");
  }

  @Test
  public void validateOtp_error() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(get(urlMatching("/otp/verify/country/57/phonenumber/3162212121/token/1111"))
      .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(OtpValidationException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_VALIDATE_OTP);
    ;
    Map<String, String> headers = new HashMap<>();
    //TODO: Set headers if needed e.g. headers.put("awesomeheader","12345");
    retrofitOtpOperations.validateOtp(headers, "57", "3162212121", "1111");
  }

  @Test
  public void testEmailOtpGeneration() throws JsonProcessingException {
    OtpResponse otpResponse = new OtpResponse();
    ObjectMapper objectMapper = new ObjectMapper();
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/generate/email/mail@mail.com")).willReturn(
      aResponse().withStatus(HttpStatus.OK.value()).withBody(objectMapper.writeValueAsString(otpResponse))
        .withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertTrue(retrofitOtpOperations.generateEmailOtp(headers, "mail@mail.com"));
  }

  @Test
  public void testOtpEmailGeneration_Error_Generating_OTP() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/generate/email/mail@mail.com")).willReturn(
      aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()).withBody("")
        .withHeader("Content-Type", "application/json")));
    exceptionRule.expect(ServiceException.class);
    exceptionRule.expectMessage("");
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperations.generateEmailOtp(headers, "mail@mail.com");
  }

  @Test
  public void testOtpEmailValidation() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(get(urlMatching("/otp/verify/email/mail@mail.com/token/1111")).willReturn(
      aResponse().withStatus(HttpStatus.OK.value()).withBody("").withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertTrue(retrofitOtpOperations.validateEmailOtp(headers, "mail@mail.com", "1111"));
  }

  @Test
  public void testOtpEmailValidation_OTP_Invalid() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(get(urlMatching("/otp/verify/email/mail@mail.com/token/1111")).willReturn(
      aResponse().withStatus(HttpStatus.NOT_ACCEPTABLE.value()).withBody("")
        .withHeader("Content-Type", "application/json")));
    Map<String, String> headers = new HashMap<>();
    assertFalse(retrofitOtpOperations.validateEmailOtp(headers, "mail@mail.com", "1111"));
  }

  @Test
  public void validateOtpEmail_error() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(get(urlMatching("/otp/verify/email/mail@mail.com/token/1111"))
      .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(OtpValidationException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_VALIDATE_EMAIL_OTP);
    ;
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperations.validateEmailOtp(headers, "mail@mail.com", "1111");
  }

  @Test
  public void validateAuthorizationSacOtp_error() throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();
    ValidateAuthorizationSacRequest validateAuthorizationSacRequest = getValidateAuthorizationSacRequest();
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/verify/authorization-sac"))
            .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(AuthorizationSacException.class);
    exceptionRule.expectMessage(ERROR_MESSAGE_VALIDATE_AUTHORIZATION_SAC);
    Map<String, String> headers = new HashMap<>();
    retrofitOtpOperations.validateAuthorizationSac(headers, validateAuthorizationSacRequest);

  }

  @Test
  public void validateAuthorizationSacOtp_Ok() throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();
    ValidateAuthorizationSacRequest validateAuthorizationSacRequest = getValidateAuthorizationSacRequest();
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/verify/authorization-sac"))
            .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
    Map<String, String> headers = new HashMap<>();
    Boolean response = retrofitOtpOperations.validateAuthorizationSac(headers, validateAuthorizationSacRequest);
    assertTrue(response);

  }

  @Test
  public void validateAuthorizationSacOtp_Forbidden() throws JsonProcessingException {

    ObjectMapper objectMapper = new ObjectMapper();
    ValidateAuthorizationSacRequest validateAuthorizationSacRequest = getValidateAuthorizationSacRequest();
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(post(urlMatching("/otp/verify/authorization-sac"))
            .willReturn(aResponse().withStatus(HttpStatus.FORBIDDEN.value())));
    Map<String, String> headers = new HashMap<>();
    Boolean response = retrofitOtpOperations.validateAuthorizationSac(headers, validateAuthorizationSacRequest);
    assertFalse(response);

  }

  @Test
  public void generateOtpIvrInternalSuccess() throws JsonProcessingException {
    authorizationIvrResponse = new AuthorizationIvrResponse();
    objectMapper = new ObjectMapper();
    authorizationIvrResponse.setOtp(OTP);


    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(
            post(urlMatching(URL_SERVICE_GENERATE_OTP))
                    .willReturn(
                            aResponse()
                                    .withStatus(HttpStatus.OK.value())
                                    .withBody(objectMapper.writeValueAsString(new Response<>(authorizationIvrResponse)))
                                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    AuthorizationIvrResponse resEntity =
            retrofitOtpOperations.generateOtpIvrInternal(new HashMap<>(), getAuthorizationIvrRequest());
    assertNotNull(resEntity);
    assertEquals(OTP, resEntity.getOtp());
  }

  @Test
  public void validateOtpIvrInternalSuccess() throws JsonProcessingException {
    validateAuthorizationIvrResponse = new ValidateAuthorizationIvrResponse(PRODUCT_NUMBER);
    objectMapper = new ObjectMapper();
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(
            post(urlMatching(URL_SERVICE_VALIDATE_OTP))
                    .willReturn(
                            aResponse()
                                    .withStatus(HttpStatus.OK.value())
                                    .withBody(objectMapper.writeValueAsString(new Response<>(validateAuthorizationIvrResponse)))
                                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    ValidateAuthorizationIvrResponse validate =
            retrofitOtpOperations.validateAuthorizationIvrInternal(new HashMap<>(), getValidateAuthorizationIvrRequest());
    assertNotNull(validate);
    assertEquals(PRODUCT_NUMBER, validate.getProductNumber());
  }

  @Test
  public void validateOtpIvrInternalException() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(
            post(urlMatching(URL_SERVICE_VALIDATE_OTP))
                    .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(AuthorizationIvrException.class);
    exceptionRule.expectMessage("Error Otp Service validateAuthorizationIvrOtpInternal");
    ValidateAuthorizationIvrResponse validate =
            retrofitOtpOperations.validateAuthorizationIvrInternal(new HashMap<>(), getValidateAuthorizationIvrRequest());
    assertEquals(null, validate.getProductNumber());
  }

  @Test
  public void generateOtpIvrInternalException() {
    RetrofitOtpOperations retrofitOtpOperations = new RetrofitOtpOperations("http://localhost:8981");
    wireMockRule.stubFor(
            post(urlMatching(URL_SERVICE_GENERATE_OTP))
                    .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));
    exceptionRule.expect(AuthorizationIvrException.class);
    exceptionRule.expectMessage("Error Otp Service generateOtpByIvrInternal");
    AuthorizationIvrResponse resEntity =
            retrofitOtpOperations.generateOtpIvrInternal(new HashMap<>(), getAuthorizationIvrRequest());
    assertEquals(null, resEntity.getOtp());
  }

  private ValidateAuthorizationIvrRequest getValidateAuthorizationIvrRequest(){
    ValidateAuthorizationIvrRequest validateAuthorizationIvrRequest = new ValidateAuthorizationIvrRequest();
    validateAuthorizationIvrRequest.setProductNumber(PRODUCT_NUMBER);
    validateAuthorizationIvrRequest.setTransactionType(TRANSACTION_TYPE);
    validateAuthorizationIvrRequest.setOtp(OTP);
    validateAuthorizationIvrRequest.setDocumentNumber(DOCUMENT_NUMBER);
    return validateAuthorizationIvrRequest;
  }

  private AuthorizationIvrRequest getAuthorizationIvrRequest(){
    AuthorizationIvrRequest authorizationIvrRequest = new AuthorizationIvrRequest();
    authorizationIvrRequest.setTransactionType(TRANSACTION_TYPE);
    authorizationIvrRequest.setProductNumber(PRODUCT_NUMBER);
    authorizationIvrRequest.setDocumentNumber(DOCUMENT_NUMBER);
    return authorizationIvrRequest;
  }

  private ValidateAuthorizationSacRequest getValidateAuthorizationSacRequest() {
    ValidateAuthorizationSacRequest validateAuthorizationSacRequest = new ValidateAuthorizationSacRequest();
    validateAuthorizationSacRequest.setAgentId("1");
    validateAuthorizationSacRequest.setClientId("2");
    validateAuthorizationSacRequest.setTransactionType(TransactionTypeSac.LOCK_ACCOUNT_SAC.name());
    validateAuthorizationSacRequest.setOtp("1234");
    validateAuthorizationSacRequest.setProductNumber("6543");
    return validateAuthorizationSacRequest;
  }
}
