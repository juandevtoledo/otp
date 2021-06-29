package com.lulobank.otp.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.otp.sdk.dto.external.VerifyExternalOperationResponse;
import com.lulobank.otp.sdk.dto.external.VerifyWithdrawalOperation;
import com.lulobank.otp.services.features.otp.VerifyExternalOperationHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class VerifyCardlessWithdrawalAdapter {
  private final VerifyExternalOperationHandler handler;

  public VerifyCardlessWithdrawalAdapter(VerifyExternalOperationHandler handler) {
    this.handler = handler;
  }

  @PostMapping("/operations/withdrawal/verify")
  public ResponseEntity<VerifyExternalOperationResponse> verifyWithdrawl(@RequestHeader final HttpHeaders headers,
                                           @RequestBody VerifyWithdrawalOperation operation) {
    operation.findOperation().setOperationId("withdrawal");
    Response<VerifyExternalOperationResponse> response = this.handler.handle(operation);
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok().build();
  }
}
