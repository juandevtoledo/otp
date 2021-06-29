package com.lulobank.otp.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.Validator;
import com.lulobank.otp.sdk.dto.VerifyOperationResponse;
import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;
import com.lulobank.otp.services.features.otp.ValidateOtpForNewLoanCommand;
import com.lulobank.otp.services.features.otp.VerifyOperationHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class VerifyHireCreditOperationAdapter {

  private final VerifyOperationHandler verifyOperationHandler;

  @Value("${otp.validation.mock}")
  private boolean mockVerify;

  @Value("${otp.validation.code}")
  private String mockOTP;

  public VerifyHireCreditOperationAdapter(VerifyOperationHandler verifyOperationHandler) {
    this.verifyOperationHandler = verifyOperationHandler;
  }

  @PostMapping("/operations/{idClient}/hire-loan/verify")
  public ResponseEntity<VerifyOperationResponse> verifyCredit(@RequestHeader final HttpHeaders headers,
                                        @RequestBody ValidateOtpForNewLoan requestPayload,
                                        @PathVariable String idClient) {

    ValidateOtpForNewLoanCommand command = new ValidateOtpForNewLoanCommand(
            idClient,
            requestPayload.getIdCredit(),
            requestPayload.getIdOffer(),
            requestPayload.getOtp());

    // Mock Activation, if mockVerify is "True" and otp = "1111" activate 200 OK verification.
    return (mockVerify) ? validateMockOTP(command) : validateOTP(command);
  }

  @NotNull
  private ResponseEntity<VerifyOperationResponse> validateOTP(ValidateOtpForNewLoanCommand command) {
    List<Validator<ValidateOtpForNewLoanCommand>> validators = Collections.emptyList();
    Response<VerifyOperationResponse> response = new ValidatorDecoratorHandler<>(this.verifyOperationHandler, validators).handle(command);

    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().build();
  }

  @NotNull
  private ResponseEntity<VerifyOperationResponse> validateMockOTP(ValidateOtpForNewLoanCommand command) {
    if(command.getOtp().equals(mockOTP)) return ResponseEntity.ok().build();
    return ResponseEntity.notFound().build();
  }
}
