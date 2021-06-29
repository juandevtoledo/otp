package com.lulobank.otp.services.features.otp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.otp.sdk.dto.OperationResponse;
import com.lulobank.otp.services.actions.OtpOperationMessage;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.utils.exception.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.lulobank.otp.services.Constants.ID_CLIENT;
import static com.lulobank.otp.services.Constants.ID_CREDIT;
import static com.lulobank.otp.services.Constants.ID_OFFER;
import static com.lulobank.otp.services.Constants.OTP_EXPIRATION;
import static com.lulobank.otp.services.Constants.OTP_LENGTH;
import static com.lulobank.otp.services.Constants.OTP_MESSAGE;
import static com.lulobank.otp.services.Constants.OTP_NUMBER;
import static com.lulobank.otp.services.utils.ErrorsOTP.OTP_VALIDATION_ERROR;
import static com.lulobank.otp.services.utils.ErrorsOTP.SERVICE_ERROR;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

public class InitOperationHandlerTest {

    private static final String CLIENT_INFORMATION = "src/test/resources/mock/hirecredit/ClientInfo.json";

    @Mock
    private OtpRepository otpRepository;
    private Map<String, OtpOperationMessage> operationMessageMap;
    @Mock
    private OtpService otpService;
    @Mock
    private RetrofitClientOperations retrofitClientOperations;

    private InitOperationHandler initOperationHandler;

    private GenerateOtpForNewLoanCommand otpForNewLoanCommand;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        operationMessageMap = new HashMap<>();
        OtpOperationMessage otpOperationMessage = new OtpOperationMessage();
        otpOperationMessage.setLength(OTP_LENGTH);
        otpOperationMessage.setExpires(OTP_EXPIRATION);
        otpOperationMessage.setMessage(OTP_MESSAGE);
        operationMessageMap.put("hire-loan", otpOperationMessage);

        initOperationHandler = new InitOperationHandler(otpRepository, operationMessageMap,
                otpService, retrofitClientOperations);

        otpForNewLoanCommand = new GenerateOtpForNewLoanCommand();
        otpForNewLoanCommand.setIdClient(ID_CLIENT);
        otpForNewLoanCommand.setIdCredit(ID_CREDIT);
        otpForNewLoanCommand.setIdOffer(ID_OFFER);
    }

    @Test
    public void shouldReturnResponseOk() throws IOException {
        when(retrofitClientOperations.getClientByIdClient(anyMap(), anyString()))
                .thenReturn(loadJson(CLIENT_INFORMATION, ClientInformationByIdClient.class));
        when(otpService.create(4)).thenReturn(OTP_NUMBER);

        Response<OperationResponse> handle = initOperationHandler.handle(otpForNewLoanCommand);
        OperationResponse operationResponse = handle.getContent();

        assertThat(operationResponse, notNullValue());
        assertThat(operationResponse.getPhone(), is("3991012048"));
        assertThat(operationResponse.getPrefix(), is("57"));
        assertThat(operationResponse.getChallenge(), is(OTP_NUMBER));
        assertThat(operationResponse.getMessage(), is(OTP_MESSAGE.concat(OTP_NUMBER)));
    }

    @Test
    public void shouldFailWhenGetClientInformationFails() throws IOException {
        when(otpService.create(4)).thenReturn(OTP_NUMBER);
        when(retrofitClientOperations.getClientByIdClient(anyMap(), anyString()))
                .thenThrow(ServiceException.class);

        Response<OperationResponse> response = initOperationHandler.handle(otpForNewLoanCommand);

        assertThat(response.getContent(), nullValue());
        assertThat(response.getHasErrors(), is(true));
        ValidationResult validationResult = response.getErrors().get(0);
        assertThat(validationResult, notNullValue());
        assertThat(validationResult.getFailure(), is(SERVICE_ERROR));
        assertThat(validationResult.getValue(), is(String.valueOf(INTERNAL_SERVER_ERROR.value())));
    }

    @Test
    public void shouldFailWhenClientInformationIsNull() throws IOException {
        when(otpService.create(4)).thenReturn(OTP_NUMBER);
        when(retrofitClientOperations.getClientByIdClient(anyMap(), anyString()))
                .thenReturn(null);

        Response<OperationResponse> response = initOperationHandler.handle(otpForNewLoanCommand);

        assertThat(response.getContent(), nullValue());
        assertThat(response.getHasErrors(), is(true));
        ValidationResult validationResult = response.getErrors().get(0);
        assertThat(validationResult, notNullValue());
        assertThat(validationResult.getFailure(), is(OTP_VALIDATION_ERROR));
        assertThat(validationResult.getValue(), is(String.valueOf(NOT_ACCEPTABLE.value())));
    }


    private <T> T loadJson(String path, Class<T> valueType) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(new File(path), valueType);
    }
}