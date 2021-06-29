package com.lulobank.otp.starter;

import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.starter.utils.LuloBearerTokenRequestPostProcessor;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WithdrawalOperationIntegrationTest extends AbstractBaseIntegrationTest {
  private static final String HIRE_CREDIT_OPERATION_OTP = "353535";
  private static final String HIRE_CREDIT_OPERATION_ERROR_OTP = "253535";
  @Value("classpath:mocks/external-operation/external-operation.request.json")
  private Resource operationJsonRequest;
  @Value("classpath:mocks/external-operation/verify-external-operation.request.json")
  private Resource verifyOperationJsonRequest;
  @Value("classpath:mocks/operations/response/external-operation.response.json")
  private Resource operationJsonRespnse;

  private static LuloBearerTokenRequestPostProcessor bearerToken() {
    return new LuloBearerTokenRequestPostProcessor();
  }

  @Override
  protected void init() {
  }

  @Test
  public void testInitHireCreditOperationSuccessfulTest() throws Exception {
    Mockito.when(otpService.create(anyInt())).thenReturn(HIRE_CREDIT_OPERATION_OTP);
    mockMvc.perform(MockMvcRequestBuilders.post("/operations/withdrawal").with(bearerToken())
                      .content(FileUtils.readFileToString(operationJsonRequest.getFile(), StandardCharsets.UTF_8))
                      .contentType("application/json")).andExpect(status().isOk()).andExpect(
      content().json(FileUtils.readFileToString(operationJsonRespnse.getFile(), StandardCharsets.UTF_8), false));
  }

  @Test
  public void testVerifyOperationSuccessfulTest() throws Exception {
    Mockito.when(otpService.create(anyInt())).thenReturn(HIRE_CREDIT_OPERATION_OTP);
    mockMvc.perform(MockMvcRequestBuilders.post("/operations/withdrawal").with(bearerToken())
                      .content(FileUtils.readFileToString(operationJsonRequest.getFile(), StandardCharsets.UTF_8))
                      .contentType("application/json")).andExpect(status().isOk()).andExpect(
      content().json(FileUtils.readFileToString(operationJsonRespnse.getFile(), StandardCharsets.UTF_8), false));
    when(otpRepository.findById(anyString()))
      .thenReturn(Optional.of(new OtpRedisEntity("123", HIRE_CREDIT_OPERATION_OTP, 1)));
    mockMvc.perform(MockMvcRequestBuilders.post("/operations/withdrawal/verify").with(bearerToken())
                      .content(FileUtils.readFileToString(verifyOperationJsonRequest.getFile(), StandardCharsets.UTF_8))
                      .contentType("application/json")).andExpect(status().isOk());
  }

  @Test
  public void testVerifyOperationFailsIfOtpDoesNotExistTest() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/operations/withdrawal/verify").with(bearerToken())
                      .content(FileUtils.readFileToString(verifyOperationJsonRequest.getFile(), StandardCharsets.UTF_8))
                      .contentType("application/json")).andExpect(status().isNotFound());
  }
}
