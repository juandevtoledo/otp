package com.lulobank.otp.starter;

import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClientContent;
import com.lulobank.otp.sdk.dto.credits.GenerateOtpForNewLoan;
import com.lulobank.otp.sdk.dto.credits.HireCreditConditions;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.services.outbounadadapters.services.IMessageToSend;
import com.lulobank.otp.starter.utils.LuloBearerTokenRequestPostProcessor;
import com.lulobank.utils.exception.ServiceException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static com.lulobank.otp.starter.utils.Constants.ID_CLIENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class InitOtpOperationTest extends AbstractBaseIntegrationTest {
    private static final String HIRE_CREDIT_OPERATION_OTP = "3535";
    private static final String HIRE_CREDIT_OPERATION_ERROR_OTP = "2535";
    private ClientInformationByIdClient clientInformationByIdClient;
    @Value("classpath:mocks/operations/request/hire-loan.operation.request.json")
    private Resource operationJsonRequest;
    @Value("classpath:mocks/operations/request/hire-loan.verify.request.json")
    private Resource verifyOperationJsonRequest;
    @Value("classpath:mocks/external/clients/clients.idClient.response.json")
    private Resource clientsGetClientJsonResponse;

    private static LuloBearerTokenRequestPostProcessor bearerToken() {
        return new LuloBearerTokenRequestPostProcessor();
    }

    @Override
    protected void init() {
        clientInformationByIdClient = new ClientInformationByIdClient();
        ClientInformationByIdClientContent clientInformationByIdClientContent = new ClientInformationByIdClientContent();
        clientInformationByIdClientContent.setIdClient(ID_CLIENT);
        clientInformationByIdClientContent.setEmailAddress("lulobanky24@yopmail.com");
        clientInformationByIdClientContent.setPhonePrefix(57);
        clientInformationByIdClientContent.setPhoneNumber("3056666777");
        clientInformationByIdClient.setContent(clientInformationByIdClientContent);
    }

    @Test
    public void testInitHireCreditOperationSuccessfulTest() throws Exception {
        when(otpService.create(anyInt())).thenReturn(HIRE_CREDIT_OPERATION_OTP);
        when(retrofitClientOperations.getClientByIdClient(anyMap(), any(String.class)))
                .thenReturn(clientInformationByIdClient);

        mockWebServer.enqueueGet("/clients/idClient/" + ID_CLIENT, HttpStatus.OK,
                FileUtils.readFileToString(clientsGetClientJsonResponse.getFile(), StandardCharsets.UTF_8));
        mockMvc.perform(post("/client/" + ID_CLIENT + "/hire-loan").with(bearerToken())
                .content(FileUtils.readFileToString(operationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json")).andExpect(status().isOk());

        verify(notifySMSSqsAdapter, times(1)).sendMessage(smsNotificationMessageCaptor.capture());;

        verify(otpService, times(1)).create(anyInt());

        assertEquals("3056666777", smsNotificationMessageCaptor.getValue().getPhoneNumber());
        assertEquals("57",
                smsNotificationMessageCaptor.getValue().getPhonePrefix());

        // verify if it was effectively saved in the database
        HireCreditConditions conditions = new HireCreditConditions();
        conditions.setAmount(new BigDecimal("5000000.0"));
        GenerateOtpForNewLoan operation = new GenerateOtpForNewLoan();
    }

    @Test
    public void testCheckOtpSuccessful() throws Exception {
        when(otpService.create(anyInt())).thenReturn(HIRE_CREDIT_OPERATION_OTP);
        when(retrofitClientOperations.getClientByIdClient(anyMap(), any(String.class)))
                .thenReturn(clientInformationByIdClient);

        mockWebServer.enqueueGet("/clients/idClient/" + ID_CLIENT, HttpStatus.OK,
                FileUtils.readFileToString(clientsGetClientJsonResponse.getFile(), StandardCharsets.UTF_8));
        mockMvc.perform(post("/client/" + ID_CLIENT + "/hire-loan")
                .with(bearerToken())
                .content(FileUtils.readFileToString(operationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json"))
                .andExpect(status().isOk());

        when(otpRepository.findById(anyString()))
                .thenReturn(Optional.of(new OtpRedisEntity("111", HIRE_CREDIT_OPERATION_OTP, 1)));
        mockMvc.perform(post("/operations/" + ID_CLIENT + "/hire-loan/verify")
                .with(bearerToken())
                .content(FileUtils.readFileToString(verifyOperationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckOtpNotFoundIfOtpDoesNotMatch() throws Exception {
        when(otpService.create(anyInt())).thenReturn(HIRE_CREDIT_OPERATION_ERROR_OTP);
        when(retrofitClientOperations.getClientByIdClient(anyMap(), any(String.class))).thenReturn(clientInformationByIdClient);

        mockWebServer.enqueueGet("/clients/idClient/" + ID_CLIENT, HttpStatus.OK,
                FileUtils.readFileToString(clientsGetClientJsonResponse.getFile(), StandardCharsets.UTF_8));
        mockMvc.perform(post("/client/" + ID_CLIENT + "/hire-loan")
                .with(bearerToken())
                .content(FileUtils.readFileToString(operationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/operations/" + ID_CLIENT + "/hire-loan/verify")
                .with(bearerToken())
                .content(FileUtils.readFileToString(verifyOperationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCheckOtpNotFoundIfOtpDoesNotExist() throws Exception {
        mockMvc.perform(post("/operations/" + ID_CLIENT + "/hire-loan/verify")
                .with(bearerToken())
                .content(FileUtils.readFileToString(verifyOperationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testForbiddenWhenClientIdDoesNotMatch() throws Exception {
        mockMvc.perform(post("/client/" + UUID.randomUUID().toString() + "/hire-loan")
                .with(bearerToken())
                .content(FileUtils.readFileToString(operationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testInternalErrorWhenGetClientInformationFails() throws Exception {
        when(otpService.create(anyInt())).thenReturn(HIRE_CREDIT_OPERATION_OTP);
        when(retrofitClientOperations.getClientByIdClient(anyMap(), any(String.class)))
                .thenThrow(ServiceException.class);

        mockMvc.perform(post("/client/" + ID_CLIENT + "/hire-loan")
                .with(bearerToken())
                .content(FileUtils.readFileToString(operationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testForbiddenWhenGetClientInformationEmpty() throws Exception {
        when(otpService.create(anyInt())).thenReturn(HIRE_CREDIT_OPERATION_OTP);
        when(retrofitClientOperations.getClientByIdClient(anyMap(), any(String.class)))
                .thenReturn(null);

        mockMvc.perform(post("/client/" + ID_CLIENT + "/hire-loan")
                .with(bearerToken())
                .content(FileUtils.readFileToString(operationJsonRequest.getFile(), StandardCharsets.UTF_8))
                .contentType("application/json"))
                .andExpect(status().isNotAcceptable());
    }
}
