package com.lulobank.otp.starter.v3.adapters.in.deleteotptoken;

import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.starter.v3.adapters.BaseUnitTest;
import com.lulobank.otp.starter.v3.adapters.Sample;
import com.lulobank.otp.starter.v3.adapters.in.deleteotptoken.dto.DeleteOTPCardlessWithdrawalInboundRequest;
import com.lulobank.otp.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.otp.starter.v3.adapters.in.dto.GenericResponse;
import io.vavr.control.Either;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

public class DeleteOTPCardlessWithdrawalInboundAdapterTest extends BaseUnitTest {

    @InjectMocks
    private DeleteOTPCardlessWithdrawalInboundAdapter testedClass;

    @Test
    public void shouldReturnErrorRemovingOtp() {
        UseCaseResponseError expectedError = Sample.buildUseCaseResponseError();
        DeleteOTPCardlessWithdrawalInboundRequest deleteOTPCardlessWithdrawalInboundRequest = Sample.buildDeleteOTPCardlessWithdrawalInboundRequest();
        when(deleteOTPCardlessWithdrawalPort.execute(any())).thenReturn(Either.left(expectedError));

        ResponseEntity<GenericResponse> response = testedClass.deleteToken(deleteOTPCardlessWithdrawalInboundRequest, bindingResult);

        assertEquals(PRECONDITION_FAILED, response.getStatusCode());

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();

        assertEquals(expectedError.getBusinessCode(), errorResponse.getCode());
        assertEquals(expectedError.getDetail(), errorResponse.getDetail());
        assertEquals(expectedError.getProviderCode(), errorResponse.getFailure());
    }

    @Test
    public void shouldReturnOkRemovingOtp() {
        DeleteOTPCardlessWithdrawalInboundRequest deleteOTPCardlessWithdrawalInboundRequest = Sample.buildDeleteOTPCardlessWithdrawalInboundRequest();
        when(deleteOTPCardlessWithdrawalPort.execute(any())).thenReturn(Either.right(true));

        ResponseEntity<GenericResponse> response = testedClass.deleteToken(deleteOTPCardlessWithdrawalInboundRequest, bindingResult);

        assertEquals(OK, response.getStatusCode());
    }

}