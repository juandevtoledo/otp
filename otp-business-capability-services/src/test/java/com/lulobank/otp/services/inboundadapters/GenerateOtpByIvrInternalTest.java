package com.lulobank.otp.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.otp.services.features.ivr.OtpAuthorizationIvrHandler;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrResponse;
import com.lulobank.otp.services.features.zendesk.model.AuthorizationSacResponse;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenerateOtpByIvrInternalTest extends AbstractBaseUnitTest {

    private static final String TRANSACTION_TYPE = "ACCOUNT_LOCK_IVR";
    private static final String PRODUCT_NUMBER = "AAASSD33";
    private static final String LAST_PRODUCT_NUMBER = "SD33";
    private static final String OTP = "456";
    private static final String DOCUMENT_NUMBER = "999999";


    @Mock
    private OtpAuthorizationIvrHandler otpAuthorizationIvrHandler;

    private OtpAuthorizationIvrHandler testHandler;

    private AuthorizationIvrRequest authorizationIvrRequest;

    private IvrAdapter testAdapter;

    @Override
    protected void init() {

        testAdapter = new IvrAdapter(otpAuthorizationIvrHandler,null);
        testHandler = new OtpAuthorizationIvrHandler(otpIvrRepository);

        authorizationIvrRequest = new AuthorizationIvrRequest();
        authorizationIvrRequest.setTransactionType(TRANSACTION_TYPE);
        authorizationIvrRequest.setProductNumber(PRODUCT_NUMBER);
        authorizationIvrRequest.setDocumentNumber(DOCUMENT_NUMBER);
    }

    @Test
    public void shouldReturnNotFoundAdapter() {
        Response response = new Response<>(getListValidations("NOT_FOUND", String.valueOf(HttpStatus.NOT_FOUND.value())));
        when(otpAuthorizationIvrHandler.handle(any())).thenReturn(response);
        ResponseEntity<Response<AuthorizationIvrResponse>> responseEntity =
                testAdapter.generateOtpByIvrInternal(new HttpHeaders(), authorizationIvrRequest);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void shouldReturnInternalServerErrorClassAdapter() {
        Response response = new Response<>(getListValidations("INTERNAL_SERVER_ERROR", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        when(otpAuthorizationIvrHandler.handle(any())).thenReturn(response);
        ResponseEntity<Response<AuthorizationIvrResponse>> responseEntity = testAdapter.generateOtpByIvrInternal(
                new HttpHeaders(), authorizationIvrRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void shouldOkClassAdapter() {
        Response response = new Response<>(new AuthorizationSacResponse());
        when(otpAuthorizationIvrHandler.handle(any())).thenReturn(response);
        ResponseEntity<Response<AuthorizationIvrResponse>> responseEntity = testAdapter.generateOtpByIvrInternal(
                new HttpHeaders(), authorizationIvrRequest);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void authorizationIvrHandlerShouldReturnOk() {
        Response response = testHandler.handle(authorizationIvrRequest);
        assertEquals(AuthorizationIvrResponse.class, response.getContent().getClass());
        AuthorizationIvrResponse authorizationIvrResponse = (AuthorizationIvrResponse) response.getContent();
        assertNotNull(authorizationIvrResponse.getOtp());
        verify(otpIvrRepository, times(1)).save(otpIvrRedisEntityArgumentCaptor.capture());
        assertEquals(authorizationIvrResponse.getOtp(), otpIvrRedisEntityArgumentCaptor.getValue().getOtp());
        assertEquals(new StringBuilder(DOCUMENT_NUMBER).append(TRANSACTION_TYPE).append(LAST_PRODUCT_NUMBER).toString(),
                otpIvrRedisEntityArgumentCaptor.getValue().getId());
    }


}
