package com.lulobank.otp.starter.v3.adapters.in.sac;


import com.lulobank.otp.services.v3.domain.zendesk.AuthorizationSac;
import com.lulobank.otp.services.v3.port.in.zendesk.SacGenerateAuthorizationPort;
import com.lulobank.otp.starter.v3.adapters.in.dto.GenericResponse;
import com.lulobank.otp.starter.v3.adapters.in.util.AdapterResponseUtil;
import com.lulobank.otp.starter.v3.adapters.in.util.ValidationUtil;
import com.lulobank.otp.starter.v3.adapters.in.sac.dto.AuthorizationSacRequest;
import com.lulobank.otp.starter.v3.adapters.in.sac.mapper.ZendeskControllerMapper;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
public class GenerateAuthorizationSacController {

    private final SacGenerateAuthorizationPort sacGenerateAuthorizationPort;


    @PostMapping(value = "/authorization-sac", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenericResponse> generateOtpByEmailAuthorizationSac(
            @RequestHeader final HttpHeaders headers,
            @Valid @RequestBody AuthorizationSacRequest authorizationSacRequest,
            BindingResult bindingResult) {
        return  Option.of(bindingResult)
                .filter(BindingResult::hasErrors)
                .map(ValidationUtil::getResponseBindingResult)
                .map(AdapterResponseUtil::badRequest)
                .getOrElse(() -> processAuthorizationSac(headers,authorizationSacRequest));

    }

    private ResponseEntity<GenericResponse> processAuthorizationSac(HttpHeaders headers, AuthorizationSacRequest authorizationSacRequest) {
        AuthorizationSac command = ZendeskControllerMapper.INSTANCE.toAuthorizationSac(authorizationSacRequest);
        command.setHttpHeaders(headers.toSingleValueMap());
        return sacGenerateAuthorizationPort.execute(command)
                .map(r -> AdapterResponseUtil.ok())
                .getOrElseGet(error -> error(ZendeskControllerMapper.INSTANCE.toErrorResponse(error),
                        getHttpStatusFromBusinessCode(error.getBusinessCode())));
    }

}
