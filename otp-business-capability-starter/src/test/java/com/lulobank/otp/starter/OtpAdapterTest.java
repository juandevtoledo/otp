package com.lulobank.otp.starter;

import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import com.lulobank.otp.starter.utils.AWSCognitoBearerTokenRequestPostProcessor;
import com.lulobank.otp.starter.utils.LuloBearerTokenRequestPostProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ActiveProfiles(profiles = "test")
public class OtpAdapterTest extends AbstractBaseIntegrationTest {
    private static final String tenantAWSToken = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUyMjMxIiwic3ViIjoic3ViamVjdCIsInNjb3BlIjoiaW50ZXJuYWxfYXBpL2ludGVybmFsX3RyYW5zYWN0aW9ucyIsImV4cCI6NDY4MzgwNTE0MX0.Ns9G1cQD5SuW0_7JICaOmHqJed2iYJ9IrtL-3r7AHFETs08b1iEjmsKrEnNhezPAnEqfholtxN_gYRbIrgYeOCvBGlNbUTmHnMsufUXyfGnRm1jRjwD4TuMpSeQLAYCmlxh4ewhB7Na4l_bZE7Kn2fdWgblVnpuB64IMYaVPYPHhqRdpm0PWflfg243M6-wAjNlEMeNGZbNm1qbTvSxjfZw9Rt8G1HwG80IG1JOGqesw_0TucvI5VseLUfTqxm5yV0xvvc_2c_8x7iiaSAAsOetHXDVyPsZv_d9D003d1d-jhJlI7ac4F8w7rrA7ng8LyPNzBUDC5EbQfDguYDglMg";

  private static final String CONTENT_TYPE = "application/json";
  private static final String SEND_OTP_BY_EMAIL_URL = "/generate/email/";
  private static final String VERIFY_EMAIL_URL = "/verify/email/%s/token/%s";
  private static final String GENERATE_OTP_WITH_PHONE_NUMBER_URL = "/generate/country/%s/phonenumber/%s";
  private static final String VERIFY_OTP_WITH_PHONE_NUMBER_URL = "/verify/country/%s/phonenumber/%s/token/%s";
  private static final String PREFIX = "57";
  private static final String PHONE_NUMBER = "3004002020";
  private static final String OTP_ENTITY_ID = "1234";
  private static final String OTP_NUMBER = "1111";
  private static final int EXPIRATION = 10;
  private static final String DESTINATION_EMAIL = "lulobanky24@yopmail.com";
  private static final String OTP = "1111";
  private EmailTemplate emailTemplate;

    @Override
    public void init() {
        MockitoAnnotations.initMocks(this);
        emailTemplate = new EmailTemplate();
        emailTemplate.getTo().add(DESTINATION_EMAIL);
    }

    @Test
    public void shouldReturnPreconditionFailedSinceWrongEmailFormat() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(SEND_OTP_BY_EMAIL_URL + "lulobanky24@yopmail").with(bearerTokenLulo())
                .header("autenticationToken", "test-authentication-token")
                .contentType(CONTENT_TYPE)).andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnOKSinceGenerateOtpByEmail() throws Exception {
        doNothing().when(mailSender).sendEmail(any(EmailTemplate.class));
        mockMvc.perform(MockMvcRequestBuilders.post(SEND_OTP_BY_EMAIL_URL + emailTemplate.getTo().get(0)).with(bearerTokenAWS())
                .header("autenticationToken", "test-authentication-token")
                .contentType(CONTENT_TYPE)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnFailedSinceWrongOtpSent() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/verify/email/" + emailTemplate.getTo().get(0) + "/token/1111").with(bearerTokenLulo())
                        .header("autenticationToken", "test-authentication-token")
                        .contentType(CONTENT_TYPE)).andExpect(status().is4xxClientError());
    }

    @Test
    public void shouldReturnOkRightOtpSent() throws Exception {
        Mockito.when(otpRepository.findById(anyString())).thenReturn(Optional.of(new OtpRedisEntity("111", OTP, 1)));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/verify/email/" + emailTemplate.getTo().get(0) + "/token/1111").with(bearerTokenAWS())
                        .header("autenticationToken", "test-authentication-token")
                        .contentType(CONTENT_TYPE)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkWhenGenerateOTPWithPhoneNumber() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(String.format(GENERATE_OTP_WITH_PHONE_NUMBER_URL, PREFIX, PHONE_NUMBER))
                .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnInternalServerErrorWhenGenerateOTPWithPhoneNumber() throws Exception {
        doThrow(new IllegalStateException("e")).when(otpRepository).save(any(OtpRedisEntity.class));
        mockMvc.perform(MockMvcRequestBuilders.post(String.format(GENERATE_OTP_WITH_PHONE_NUMBER_URL, PREFIX, PHONE_NUMBER))
                .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isInternalServerError());
    }

    @Test
    public void shouldReturnPreconditionFailedSincePrefixHasWrongFormatWhenGenerateOTP() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(String.format(GENERATE_OTP_WITH_PHONE_NUMBER_URL, "3535", PHONE_NUMBER))
                .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isPreconditionFailed());
    }

    @Test
    public void shouldReturnPreconditionFailedSincePhoneNumberHasWrongPhoneNumberFormatWhenGenerateOTP() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(String.format(GENERATE_OTP_WITH_PHONE_NUMBER_URL, PREFIX, "2634882"))
                .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isPreconditionFailed());
    }

    @Test
    public void shouldReturnPreconditionFailedSincePhoneNumberHasWrongFirstPhoneNumberWhenGenerateOTP() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(String.format(GENERATE_OTP_WITH_PHONE_NUMBER_URL, PREFIX, "2192568344"))
                .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isPreconditionFailed());
    }

    @Test
    public void shouldReturnOkSinceOTPShouldVerifiedWhenVerifyOTP() throws Exception {
        doReturn(Optional.of(new OtpRedisEntity(OTP_ENTITY_ID, OTP_NUMBER, EXPIRATION))).when(otpRepository)
                .findById(any(String.class));
        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format(VERIFY_OTP_WITH_PHONE_NUMBER_URL, PREFIX, PHONE_NUMBER, OTP_NUMBER))
                        .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotAcceptableSinceOTPNotExistVerifyOTP() throws Exception {
        when(otpRepository.findById(any(String.class))).thenReturn(Optional.empty());
        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format(VERIFY_OTP_WITH_PHONE_NUMBER_URL, PREFIX, PHONE_NUMBER, OTP_NUMBER))
                        .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldReturnNotAcceptableSinceOTPAreDifferentsVerifyOTP() throws Exception {
        when(otpRepository.findById(any(String.class)))
                .thenReturn(Optional.of(new OtpRedisEntity(OTP_ENTITY_ID, "9087", EXPIRATION)));
        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format(VERIFY_OTP_WITH_PHONE_NUMBER_URL, PREFIX, PHONE_NUMBER, OTP_NUMBER))
                        .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldReturnNotAcceptableSinceOTPNotExistVerifyOTPWithEmail() throws Exception {
        when(otpRepository.findById(any(String.class))).thenReturn(Optional.empty());
        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format(VERIFY_EMAIL_URL,DESTINATION_EMAIL, OTP_NUMBER))
                        .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isNotAcceptable());
    }

    @Test
    public void shouldReturPreconditionFailedSinceOTPHasWrongFormatWhenGenerateOTP() throws Exception {
        when(otpRepository.findById(any(String.class)))
                .thenReturn(Optional.of(new OtpRedisEntity(OTP_ENTITY_ID, OTP_NUMBER, EXPIRATION)));
        mockMvc.perform(MockMvcRequestBuilders.get(String.format(VERIFY_OTP_WITH_PHONE_NUMBER_URL, PREFIX, PHONE_NUMBER, "111"))
                .with(bearerTokenAWS()).contentType(CONTENT_TYPE)).andExpect(status().isPreconditionFailed());
    }

  private static LuloBearerTokenRequestPostProcessor bearerTokenLulo() {
    return new LuloBearerTokenRequestPostProcessor();
  }

  private static AWSCognitoBearerTokenRequestPostProcessor bearerTokenAWS() {
    return new AWSCognitoBearerTokenRequestPostProcessor(tenantAWSToken);
  }
}
