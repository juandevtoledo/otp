package com.lulobank.otp.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.otp.services.features.ivr.OtpAuthorizationIvrHandler;
import com.lulobank.otp.services.features.ivr.ValidateAuthorizationIvrHandler;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrResponse;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;


import static com.lulobank.core.utils.ValidatorUtils.getHttpStatusByCode;

@RestController
@RequestMapping("/")
public class IvrAdapter {

    @Autowired
    private OtpAuthorizationIvrHandler otpAuthorizationIvrHandler;

    @Autowired
    private ValidateAuthorizationIvrHandler validateAuthorizationIvrHandler;

    public IvrAdapter(OtpAuthorizationIvrHandler otpAuthorizationIvrHandler,
                      ValidateAuthorizationIvrHandler validateAuthorizationIvrHandler) {
        this.otpAuthorizationIvrHandler = otpAuthorizationIvrHandler;
        this.validateAuthorizationIvrHandler = validateAuthorizationIvrHandler;
    }

    @PostMapping(value = "/ivrInternal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<AuthorizationIvrResponse>> generateOtpByIvrInternal(@RequestHeader final HttpHeaders headers,
                                                     @Valid @RequestBody AuthorizationIvrRequest authorizationIvrRequest) {
        authorizationIvrRequest.setHttpHeaders(headers.toSingleValueMap());
        Response<AuthorizationIvrResponse> response =
                otpAuthorizationIvrHandler.handle(authorizationIvrRequest);
        if (Boolean.TRUE.equals(response.getHasErrors())) {
            Optional<ValidationResult> validationResult = response.getErrors().stream().findFirst();
            return validationResult
                    .map(getOtpByTypeError(response, validationResult))
                    .orElseGet(getOtpErrorResult(response));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/verify/ivrInternal",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<ValidateAuthorizationIvrResponse>> validateAuthorizationIvrOtpInternal(@RequestHeader final HttpHeaders headers,
                                                                                                           @Valid @RequestBody ValidateAuthorizationIvrRequest validateAuthorizationIvrRequest) {
        validateAuthorizationIvrRequest.setHttpHeaders(headers.toSingleValueMap());
        Response<ValidateAuthorizationIvrResponse> response =
                validateAuthorizationIvrHandler.handle(validateAuthorizationIvrRequest);
        if (Boolean.TRUE.equals(response.getHasErrors())) {
            Optional<ValidationResult> validationResult = response.getErrors().stream().findFirst();
            return validationResult
                    .map(getOtpValidateByTypeError(response, validationResult))
                    .orElseGet(getOtpValidateErrorResult(response));

        }
        return ResponseEntity.ok(response);
    }

    private Function<ValidationResult, ResponseEntity<Response<ValidateAuthorizationIvrResponse>>> getOtpValidateByTypeError(
            Response<ValidateAuthorizationIvrResponse> response, Optional<ValidationResult> validationResult) {
        return validation ->
                new ResponseEntity<Response<ValidateAuthorizationIvrResponse>>(
                        new Response<ValidateAuthorizationIvrResponse>(response.getErrors()),
                        getHttpStatusByCode(validationResult.get().getValue()));
    }

    private Supplier<ResponseEntity<Response<ValidateAuthorizationIvrResponse>>> getOtpValidateErrorResult(Response<ValidateAuthorizationIvrResponse> response) {
        return () ->
                new ResponseEntity<Response<ValidateAuthorizationIvrResponse>>(
                        new Response<ValidateAuthorizationIvrResponse>(response.getErrors()),
                        getHttpStatusByCode(HttpStatus.INTERNAL_SERVER_ERROR.name()));
    }

    private Function<ValidationResult, ResponseEntity<Response<AuthorizationIvrResponse>>> getOtpByTypeError(
            Response<AuthorizationIvrResponse> response, Optional<ValidationResult> validationResult) {
        return validation ->
                new ResponseEntity<Response<AuthorizationIvrResponse>>(
                        new Response<AuthorizationIvrResponse>(response.getErrors()),
                        getHttpStatusByCode(validationResult.get().getValue()));
    }

    private Supplier<ResponseEntity<Response<AuthorizationIvrResponse>>> getOtpErrorResult(
            Response<AuthorizationIvrResponse> response) {
        return () ->
                new ResponseEntity<Response<AuthorizationIvrResponse>>(
                        new Response<AuthorizationIvrResponse>(response.getErrors()),
                        getHttpStatusByCode(HttpStatus.INTERNAL_SERVER_ERROR.name()));
    }


}
