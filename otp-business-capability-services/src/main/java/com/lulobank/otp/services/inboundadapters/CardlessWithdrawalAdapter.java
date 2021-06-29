package com.lulobank.otp.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.otp.sdk.dto.external.ExternalOperationResponse;
import com.lulobank.otp.sdk.dto.external.WithdrawalOperation;
import com.lulobank.otp.services.features.otp.InitExternalOperationHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class CardlessWithdrawalAdapter {
  private InitExternalOperationHandler initOperationHandler;

  public CardlessWithdrawalAdapter(InitExternalOperationHandler initOperationHandler) {
    this.initOperationHandler = initOperationHandler;
  }

  @PostMapping("/operations/withdrawal")
  public ResponseEntity<ExternalOperationResponse> initWithDrawalOperation(@RequestHeader final HttpHeaders headers,
                                                   @RequestBody WithdrawalOperation withDrawalIntention) {
    withDrawalIntention.setOperationId("withdrawal");
    Response<ExternalOperationResponse> response = this.initOperationHandler.handle(withDrawalIntention);
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok().body(response.getContent());
  }
}
