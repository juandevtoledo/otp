package com.lulobank.otp.starter.v3.adapters.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.lulobank.otp.services.v3.domain.OTPGenerationRs;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import com.lulobank.otp.services.v3.port.in.GenerateOTP;
import com.lulobank.otp.services.v3.services.JacksonHash;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping("/v3/{idClient}")
@Slf4j
public class GenerateOTPController {

    private GenerateOTP generateOTP;

    @Value("${otp.expiration-time-in-sec}")
    private int expirationTimeInSec;

    public GenerateOTPController(GenerateOTP generateOTP) {
        this.generateOTP = generateOTP;
    }

    @PostMapping(value = "/generate/{transactionType}")
    public ResponseEntity<OTPGenerationRs> acceptOffer(@RequestHeader final HttpHeaders headers,
                                      @PathVariable("idClient") @NotBlank(message = "") String idClient,
                                      @PathVariable("transactionType") @NotBlank(message = "") OTPTransaction transactionType,
                                      @Valid @RequestBody final JsonNode payload,
                                      BindingResult bindingResult) {

        OTPValidationRq command = new OTPValidationRq(payload, idClient, transactionType);
        command.setHttpHeaders(headers.toSingleValueMap());
        Try<String> resp = generateOTP.execute(command);
        return resp.map(otpString -> successResponseHandler(transactionType, JacksonHash.apply(payload)))
                .getOrElseGet(this::errorHandler);

    }

    private ResponseEntity<OTPGenerationRs> successResponseHandler(OTPTransaction transactionType, String hash) {
        return ResponseEntity.ok().body(
                new OTPGenerationRs(transactionType.getArrangement().getTargets().peek(),
                        transactionType.getArrangement().getLength(), expirationTimeInSec, hash));
    }

    private ResponseEntity<OTPGenerationRs> errorHandler(Throwable error) {
        log.error("Generate OTP error", error);
        return ResponseEntity.badRequest().build();
    }
}
