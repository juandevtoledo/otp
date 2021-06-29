package com.lulobank.otp.starter;

import com.lulobank.otp.starter.utils.AWSCognitoBearerTokenRequestPostProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ActiveProfiles(profiles = "dev")
public class OtpAdapterMockTest extends AbstractBaseIntegrationTest {
    private static final String tenantAWSToken = "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUyMjMxIiwic3ViIjoic3ViamVjdCIsInNjb3BlIjoiaW50ZXJuYWxfYXBpL2ludGVybmFsX3RyYW5zYWN0aW9ucyIsImV4cCI6NDY4MzgwNTE0MX0.Ns9G1cQD5SuW0_7JICaOmHqJed2iYJ9IrtL-3r7AHFETs08b1iEjmsKrEnNhezPAnEqfholtxN_gYRbIrgYeOCvBGlNbUTmHnMsufUXyfGnRm1jRjwD4TuMpSeQLAYCmlxh4ewhB7Na4l_bZE7Kn2fdWgblVnpuB64IMYaVPYPHhqRdpm0PWflfg243M6-wAjNlEMeNGZbNm1qbTvSxjfZw9Rt8G1HwG80IG1JOGqesw_0TucvI5VseLUfTqxm5yV0xvvc_2c_8x7iiaSAAsOetHXDVyPsZv_d9D003d1d-jhJlI7ac4F8w7rrA7ng8LyPNzBUDC5EbQfDguYDglMg";

  private static final String CONTENT_TYPE = "application/json";
  private static final String SEND_OTP_BY_EMAIL_URL = "/generate/email/";
  private static final String VERIFY_EMAIL_URL = "/verify/email/%s/token/%s";
  private static final String OTP_NUMBER = "1111";
  private static final String DESTINATION_EMAIL = "lulobanky24@yopmail.com";

    @Override
    public void init() {
    }

    @Test
    public void shouldReturnOkGenerateOTPEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(SEND_OTP_BY_EMAIL_URL + DESTINATION_EMAIL).with(bearerTokenAWS())
                .contentType(CONTENT_TYPE)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkValidateOTPEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(String.format(VERIFY_EMAIL_URL,DESTINATION_EMAIL,OTP_NUMBER)).with(bearerTokenAWS())
                .contentType(CONTENT_TYPE)).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnForbiddenValidateOTPEmail() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(String.format(VERIFY_EMAIL_URL,DESTINATION_EMAIL,"1234")).with(bearerTokenAWS())
                .contentType(CONTENT_TYPE)).andExpect(status().isNotAcceptable());
    }

  private static AWSCognitoBearerTokenRequestPostProcessor bearerTokenAWS() {
    return new AWSCognitoBearerTokenRequestPostProcessor(tenantAWSToken);
  }
}
