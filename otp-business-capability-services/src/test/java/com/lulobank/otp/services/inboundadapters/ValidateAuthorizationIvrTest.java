package com.lulobank.otp.services.inboundadapters;

import com.amazonaws.SdkClientException;
import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.otp.services.features.ivr.ValidateAuthorizationIvrHandler;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrResponse;
import com.lulobank.otp.services.features.zendesk.model.ValidateAuthorizationSacResponse;
import com.lulobank.otp.services.outbounadadapters.model.OtpIvrRedisEntity;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class ValidateAuthorizationIvrTest extends AbstractBaseUnitTest{

    private static final String TRANSACTION_TYPE = "ACCOUNT_LOCK_IVR";
    private static final String PRODUCT_NUMBER = "AAASSD33";
    private static final String LAST_PRODUCT_NUMBER = "SD33";
    private static final String OTP = "456";
    private static final String DOCUMENT_NUMBER = "999999";

    @Mock
    private ValidateAuthorizationIvrHandler validateAuthorizationIvrHandler;

    private IvrAdapter testAdapter;

    private ValidateAuthorizationIvrHandler testHandler;

    private ValidateAuthorizationIvrRequest validateAuthorizationIvrRequest;

    private OtpIvrRedisEntity otpIvrRedisEntity;

    private StringBuilder key;

    @Override
    protected void init() {

        testAdapter = new IvrAdapter(null,validateAuthorizationIvrHandler);
        testHandler = new ValidateAuthorizationIvrHandler(otpIvrRepository);

        validateAuthorizationIvrRequest = new ValidateAuthorizationIvrRequest();
        validateAuthorizationIvrRequest.setTransactionType(TRANSACTION_TYPE);
        validateAuthorizationIvrRequest.setProductNumber(PRODUCT_NUMBER);
        validateAuthorizationIvrRequest.setOtp(OTP);
        validateAuthorizationIvrRequest.setDocumentNumber(DOCUMENT_NUMBER);

        key =  new StringBuilder();
        key.append(DOCUMENT_NUMBER).append(TRANSACTION_TYPE).append(PRODUCT_NUMBER);
        otpIvrRedisEntity = new OtpIvrRedisEntity(key.toString(),OTP,0, LAST_PRODUCT_NUMBER);
    }

    @Test
    public void shouldReturnNotFoundAdapter() {
        Response response = new Response<>(getListValidations("FORBIDDEN", String.valueOf(HttpStatus.FORBIDDEN.value())));
        when(validateAuthorizationIvrHandler.handle(any())).thenReturn(response);
        ResponseEntity<Response<ValidateAuthorizationIvrResponse>> responseEntity =
                testAdapter.validateAuthorizationIvrOtpInternal(new HttpHeaders(), validateAuthorizationIvrRequest);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnInternalServerErrorClassAdapter() {
        Response response = new Response<>(getListValidations("INTERNAL_SERVER_ERROR", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        when(validateAuthorizationIvrHandler.handle(any())).thenReturn(response);
        ResponseEntity<Response<ValidateAuthorizationIvrResponse>> responseEntity = testAdapter.validateAuthorizationIvrOtpInternal(new HttpHeaders(),
                validateAuthorizationIvrRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnInternalOkClassAdapter() {
        Response response = new Response<>(new ValidateAuthorizationSacResponse());
        when(validateAuthorizationIvrHandler.handle(any())).thenReturn(response);
        ResponseEntity<Response<ValidateAuthorizationIvrResponse>> responseEntity = testAdapter.validateAuthorizationIvrOtpInternal(new HttpHeaders(),
                validateAuthorizationIvrRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void validateAuthorizationIvrHandlerShouldReturnForbidden() {
        validateAuthorizationIvrRequest.setOtp("4555");
        when(otpIvrRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(otpIvrRedisEntity));
        Response response = testHandler.handle(validateAuthorizationIvrRequest);
        ValidationResult validationResult = (ValidationResult) response.getErrors().stream().findFirst().get();
        assertEquals(String.valueOf(FORBIDDEN.value()), validationResult.getValue());
    }

    @Test
    public void validateAuthorizationIvrHandlerShouldReturnInternalServerErrorServiceException() {
        when(otpIvrRepository.findById(anyString())).thenThrow(SdkClientException.class);
        Response response = testHandler.handle(validateAuthorizationIvrRequest);
        ValidationResult validationResult = (ValidationResult) response.getErrors().stream().findFirst().get();
        assertEquals(String.valueOf(INTERNAL_SERVER_ERROR.value()), validationResult.getValue());
    }

    @Test
    public void validateAuthorizationSacHandlerShouldReturnOk() {
        when(otpIvrRepository.findById(anyString())).thenReturn(java.util.Optional.ofNullable(otpIvrRedisEntity));
        Response response = testHandler.handle(validateAuthorizationIvrRequest);
        verify(otpIvrRepository, times(1)).findById(stringArgumentCaptor.capture());
        verify(otpIvrRepository, times(1)).delete(otpIvrRedisEntityArgumentCaptor.capture());
        assertEquals(key.toString(), stringArgumentCaptor.getValue());
        assertEquals(key.toString(),otpIvrRedisEntity.getId());
        assertEquals(OTP,otpIvrRedisEntity.getOtp());
    }


}
