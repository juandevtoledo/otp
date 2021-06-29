package com.lulobank.otp.starter.v3.adapters.in.sac;


import com.lulobank.otp.services.v3.domain.zendesk.VerifyAuthorizationSac;
import com.lulobank.otp.services.v3.port.in.zendesk.SacVerifyAuthorizationPort;
import com.lulobank.otp.starter.v3.adapters.in.dto.GenericResponse;
import com.lulobank.otp.starter.v3.adapters.in.sac.dto.VerifyAuthorizationSacRequest;
import com.lulobank.otp.starter.v3.adapters.in.sac.mapper.ZendeskControllerMapper;
import com.lulobank.otp.starter.v3.adapters.in.util.AdapterResponseUtil;
import com.lulobank.otp.starter.v3.adapters.in.util.ValidationUtil;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lulobank.otp.starter.v3.adapters.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;
import static com.lulobank.otp.starter.v3.adapters.in.util.AdapterResponseUtil.error;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ValidateAuthorizationSacController {

    @Value("${otp.validation.mock}")
    private boolean mockVerify;
    @Value("${otp.validation.code}")
    private String mockOTP;

    private final SacVerifyAuthorizationPort sacVerifyAuthorizationPort;


    @PostMapping(value = "/verify/authorization-sac", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericResponse> verifyOtpByAuthorizationSac(
            @RequestHeader final HttpHeaders headers,
            @Valid @RequestBody VerifyAuthorizationSacRequest verifyAuthorizationSacRequest,
            BindingResult bindingResult) {
        return  Option.of(bindingResult)
                .filter(BindingResult::hasErrors)
                .map(ValidationUtil::getResponseBindingResult)
                .map(AdapterResponseUtil::badRequest)
                .getOrElse(() -> mockVerify ? getValidationMockResponse(verifyAuthorizationSacRequest) :
                        processVerifyAuthorizationSac(verifyAuthorizationSacRequest));

    }

    private ResponseEntity<GenericResponse> getValidationMockResponse(VerifyAuthorizationSacRequest verifyAuthorizationSacRequest) {
        return Option.of(verifyAuthorizationSacRequest.getOtp())
                .filter(s -> s.equals(mockOTP))
                .map(s -> AdapterResponseUtil.ok())
                .getOrElse(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<GenericResponse> processVerifyAuthorizationSac(VerifyAuthorizationSacRequest verifyAuthorizationSacRequest) {
        VerifyAuthorizationSac command = ZendeskControllerMapper.INSTANCE.toVerifyAuthorizationSac(verifyAuthorizationSacRequest);
        return sacVerifyAuthorizationPort.execute(command)
                .map(r -> AdapterResponseUtil.ok())
                .getOrElseGet(error -> error(ZendeskControllerMapper.INSTANCE.toErrorResponse(error),
                        getHttpStatusFromBusinessCode(error.getBusinessCode())));
    }



}
