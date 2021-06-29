package com.lulobank.otp.starter.v3.adapters.in;
import com.fasterxml.jackson.databind.JsonNode;
import com.lulobank.otp.services.exceptions.NotValidTransactionDataException;
import com.lulobank.otp.services.exceptions.OTPNotFoundException;
import com.lulobank.otp.services.exceptions.OTPNotMatchException;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.domain.OTPValidationRq;
import com.lulobank.otp.services.v3.domain.VerifyOTPRs;
import com.lulobank.otp.services.v3.port.in.VerifyOTP;
import com.lulobank.otp.services.v3.services.JacksonHash;
import io.vavr.API;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/v3/{idClient}")
@Slf4j
public class VerifyOTPController {

    private VerifyOTP verifyOTP;
    @Value("${otp.validation.mock}")
    private boolean mockVerify;
    public VerifyOTPController(VerifyOTP verifyOTP) {
        this.verifyOTP = verifyOTP;
    }

    @PostMapping(value = "/verify/{transactionType}")
    public ResponseEntity<VerifyOTPRs> verify(@RequestHeader final HttpHeaders headers,
                                              @PathVariable("idClient") @NotBlank(message = "") String idClient,
                                              @PathVariable("transactionType") @NotBlank(message = "") OTPTransaction transactionType,
                                              @Valid @RequestBody final JsonNode payload,
                                              BindingResult bindingResult) {


        Try<VerifyOTPRs> resp = verifyOTP.execute(new OTPValidationRq(payload, idClient, transactionType, headers.getFirst("otp-token")));
        return resp.filter(VerifyOTPRs::isValid)
                .map(ResponseEntity::ok)
                .getOrElseGet(this::errorHandler);

    }

    @PostMapping(value = "/verify/checksum/{transactionType}")
    public ResponseEntity<VerifyOTPRs> verifyChecksum(@RequestHeader final HttpHeaders headers,
                                                      @PathVariable("idClient") @NotBlank(message = "") String idClient,
                                                      @PathVariable("transactionType") @NotBlank(message = "") OTPTransaction transactionType) {
        return mockVerify ? getValidateMockResponse(headers) : processRequest(headers,idClient,transactionType);
    }
    private ResponseEntity <VerifyOTPRs> processRequest(HttpHeaders headers,String idClient, OTPTransaction transactionType) {
        return JacksonHash.processHeader(headers.getFirst("otp-token"))
                .map(e -> new OTPValidationRq(e._2(), idClient, transactionType, e._1()))
                .map(e -> {
                    Try<VerifyOTPRs> resp = verifyOTP.execute(e);
                    return resp.filter(VerifyOTPRs::isValid)
                            .map(ResponseEntity::ok)
                            .getOrElseGet(this::errorHandler);
                })
                .fold(() -> ResponseEntity.unprocessableEntity().build(), e -> e);

    }
    private ResponseEntity getValidateMockResponse(HttpHeaders headers) {
        return JacksonHash.processHeader(headers.getFirst("otp-token"))
                .map(header -> API.Match(header._1()).of(
                        Case($("1200"), e -> ok(new VerifyOTPRs(true))),
                        Case($("1403"), e -> status(HttpStatus.NOT_ACCEPTABLE).build()),
                        Case($("1404"), e -> notFound().build()),
                        Case($(), e -> status(HttpStatus.INTERNAL_SERVER_ERROR).build())
                        )
                ).fold(() -> ResponseEntity.unprocessableEntity().build(), e -> e);
    }
    private ResponseEntity errorHandler(Throwable error) {
        String idError = UUID.randomUUID().toString();
        log.error("Verify OTP error " + idError, error);
        return API.Match(error).of(
                Case($(instanceOf(OTPNotFoundException.class)), e -> notFound().build()),
                Case($(instanceOf(OTPNotMatchException.class)), e -> status(HttpStatus.NOT_ACCEPTABLE).build()),
                Case($(instanceOf(NotValidTransactionDataException.class)), e -> badRequest().body(idError + " - " + e.getLocalizedMessage())),
                Case($(), e -> status(HttpStatus.INTERNAL_SERVER_ERROR).body(idError + " - " + e.getLocalizedMessage()))
        );
    }
}
