package com.lulobank.otp.starter.v3.adapters.in.deleteotptoken;

import com.lulobank.otp.services.v3.port.in.DeleteOTPCardlessWithdrawalPort;
import com.lulobank.otp.starter.v3.adapters.in.deleteotptoken.dto.DeleteOTPCardlessWithdrawalInboundRequest;
import com.lulobank.otp.starter.v3.adapters.in.deleteotptoken.mapper.DeleteOTPCardlessWithdrawalMapper;
import com.lulobank.otp.starter.v3.adapters.in.dto.ErrorResponse;
import com.lulobank.otp.starter.v3.adapters.in.dto.GenericResponse;
import com.lulobank.otp.starter.v3.adapters.in.util.AdapterResponseUtil;
import com.lulobank.otp.starter.v3.adapters.in.util.ValidationUtil;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.lulobank.otp.starter.v3.adapters.in.mapper.InboundAdapterErrorMapper.getHttpStatusFromBusinessCode;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DeleteOTPCardlessWithdrawalInboundAdapter {

    private final DeleteOTPCardlessWithdrawalPort deleteOTPCardlessWithdrawalPort;

    @PostMapping(value = "operations/withdrawal/client/{idClient}/token/delete")
    public ResponseEntity<GenericResponse> deleteToken(
            @Valid @RequestBody final DeleteOTPCardlessWithdrawalInboundRequest deleteOTPCardlessWithdrawalInboundRequest,
            BindingResult bindingResult) {

        return Option.of(bindingResult)
                .filter(BindingResult::hasErrors)
                .map(ValidationUtil::getResponseBindingResult)
                .map(AdapterResponseUtil::badRequest)
                .getOrElse(() -> deleteOTPCardlessWithdrawalPort.execute(DeleteOTPCardlessWithdrawalMapper.toDeleteOTPTokenRequest.apply(deleteOTPCardlessWithdrawalInboundRequest))
                        .map(success -> AdapterResponseUtil.ok())
                        .getOrElseGet(error -> AdapterResponseUtil
                                .error(new ErrorResponse(error.getProviderCode(), error.getBusinessCode(), error.getDetail()),
                                        getHttpStatusFromBusinessCode(error.getBusinessCode()))));
    }
}
