package com.lulobank.otp.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.features.onboardingotp.model.OtpEmailRequest;
import com.lulobank.otp.services.features.onboardingotp.model.OtpRequest;
import com.lulobank.otp.services.utils.ErrorsOTP;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.lulobank.core.utils.ValidatorUtils.getHttpStatusByCode;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class  OtpAdapter {
  private ValidatorDecoratorHandler createOtpEmailHandler;
  private ValidatorDecoratorHandler verifyOtpEmailHandler;
  private ValidatorDecoratorHandler createOtpHandler;
  private ValidatorDecoratorHandler verifyOtpHandler;
  @Value("${otp.validation.mock}")
  private boolean smoke;
  @Value("${otp.validation.code}")
  private String mockOTP;
  @Value("${mail.sender}")
  private String emailSenderAddress;

  @Autowired
  public OtpAdapter(ValidatorDecoratorHandler createOtpEmailHandler, ValidatorDecoratorHandler verifyOtpEmailHandler,
                    ValidatorDecoratorHandler createOtpHandler, ValidatorDecoratorHandler verifyOtpHandler) {
    this.createOtpEmailHandler = createOtpEmailHandler;
    this.verifyOtpEmailHandler = verifyOtpEmailHandler;
    this.createOtpHandler = createOtpHandler;
    this.verifyOtpHandler = verifyOtpHandler;
  }

  @PostMapping(value = "/generate/country/{prefix}/phonenumber/{phonenumber}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> generateOtp(@RequestHeader final HttpHeaders headers,
                                            @PathVariable final String prefix, @PathVariable final String phonenumber) {
    if (Boolean.TRUE.equals(smoke)) return new ResponseEntity<>(HttpStatus.OK);
    Response<OtpResponse> response = createOtpHandler.handle(new OtpRequest(prefix, phonenumber));
    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(HttpStatus.OK);
    }
    if (response.getErrors().stream().anyMatch(x -> ErrorsOTP.SERVICE_ERROR.equals(x.getFailure()))) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
  }

  @PostMapping(value = "/generate/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> generateOtpByEmail(@RequestHeader final HttpHeaders headers,
                                                     @PathVariable final String email) {
    if (Boolean.TRUE.equals(smoke)) return new ResponseEntity<>(HttpStatus.OK);
    EmailTemplate emailTemplate = new EmailTemplate();
    emailTemplate.getTo().add(email);
    emailTemplate.setFrom(emailSenderAddress);
    Response response = createOtpEmailHandler.handle(new OtpEmailRequest(emailTemplate, headers.toSingleValueMap()));
    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return ResponseEntity.status(HttpStatus.OK.value()).build();
    }
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(new Response(response.getErrors()),
        getHttpStatusByCode(((ValidationResult) response.getErrors().get(0)).getValue()));
    }
    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED.value()).build();
  }

  @GetMapping(value = "/verify/country/{prefix}/phonenumber/{phonenumber}/token/{token}",
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> validateOtp(@RequestHeader final HttpHeaders headers,
                                            @PathVariable final String prefix, @PathVariable final String phonenumber, @PathVariable final String token) {
    if (Boolean.TRUE.equals(smoke)) {
      if (token.contains(mockOTP)) return new ResponseEntity<>(HttpStatus.OK);
      else return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
    Response<OtpResponse> response = verifyOtpHandler.handle(new OtpRequest(prefix, phonenumber, token));
    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(HttpStatus.OK);
    }
    if (response.getErrors().stream().anyMatch(x -> ErrorsOTP.INVALID_OTP.equals(x.getFailure()))) {
      return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
    return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
  }

  @GetMapping(value = "/verify/email/{email}/token/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Response> validateOtpSentToEmail(@RequestHeader final HttpHeaders headers,
                                                         @PathVariable String email, @PathVariable String token) {
    if (Boolean.TRUE.equals(smoke)) {
      if (token.contains(mockOTP)) return new ResponseEntity<>(HttpStatus.OK);
      else return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    }
    EmailTemplate emailTemplate = new EmailTemplate();
    emailTemplate.getTo().add(email);
    Response response = verifyOtpEmailHandler.handle(new OtpEmailRequest(emailTemplate, token, headers.toSingleValueMap()));
    if (Boolean.FALSE.equals(response.getHasErrors())) {
      return ResponseEntity.status(HttpStatus.OK.value()).build();
    }
    if (Boolean.TRUE.equals(response.getHasErrors())) {
      return new ResponseEntity<>(new Response(response.getErrors()),
        getHttpStatusByCode(((ValidationResult) response.getErrors().get(0)).getValue()));
    }
    return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED.value()).build();
  }
}
