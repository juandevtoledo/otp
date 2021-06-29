package com.lulobank.otp.services.inboundadapters;

import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.utils.ValidatorUtils;
import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.otp.sdk.dto.OperationResponse;
import com.lulobank.otp.sdk.dto.credits.GenerateOtpForNewLoan;
import com.lulobank.otp.services.actions.SendSMSOtpOperation;
import com.lulobank.otp.services.features.otp.GenerateOtpForNewLoanCommand;
import com.lulobank.otp.services.features.otp.GenerateOtpForNewLoanValidator;
import com.lulobank.otp.services.features.otp.InitOperationHandler;
import com.lulobank.otp.services.inboundadapters.mapper.GenerateOtpForNewLoanMapper;
import com.lulobank.otp.services.ports.out.messaging.MessagingPort;
import com.lulobank.otp.services.ports.out.messaging.dto.SMSNotificationMessage;
import com.lulobank.otp.services.utils.ErrorsOTP;
import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class HireCreditAdapter {

    private InitOperationHandler initOperationHandler;
    private MessagingPort<SMSNotificationMessage> messagingPort;

    @Value("${otp.validation.mock}")
    private boolean mockVerify;

    public HireCreditAdapter(InitOperationHandler initOperationHandler,
                             MessagingPort<SMSNotificationMessage> messagingPort) {
        this.initOperationHandler = initOperationHandler;
        this.messagingPort = messagingPort;
    }

    @PostMapping("/client/{idClient}/hire-loan")
    public ResponseEntity<Void> hireCreditOperation(@RequestHeader final HttpHeaders headers,
                                                    @RequestBody GenerateOtpForNewLoan request,
                                                    @PathVariable String idClient) {

        GenerateOtpForNewLoanCommand command = GenerateOtpForNewLoanMapper.INSTANCE.generateOtpForNewLoanToGenerateOtpForNewLoanCommand(request, idClient);
        command.setHttpHeaders(headers.toSingleValueMap());
        return mockVerify ? generateMockOTP() : generateOTP(command);
    }

    @NotNull
    private ResponseEntity<Void> generateOTP(GenerateOtpForNewLoanCommand command) {
        List<Action<Response<OperationResponse>, GenerateOtpForNewLoanCommand>> actions = Arrays.asList(
                new SendSMSOtpOperation(messagingPort));
        List<Validator<GenerateOtpForNewLoanCommand>> validators = Arrays.asList(
                new GenerateOtpForNewLoanValidator());

        Response<OperationResponse> response = new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler<>(this.initOperationHandler, actions), validators).handle(command);

        if (Boolean.FALSE.equals(response.getHasErrors())) return ResponseEntity.ok().build();

        return Option.ofOptional(response.getErrors().stream().findAny())
                .map(this::buildErrorResponse)
                .getOrElse(ResponseEntity.status(INTERNAL_SERVER_ERROR).build());
    }

    private ResponseEntity<Void> buildErrorResponse(ValidationResult response) {
        return ResponseEntity.status(getStatusFromError(response.getValue())).build();
    }

    private HttpStatus getStatusFromError(String value) {
        return ErrorsOTP.BAD_REQUEST.equalsIgnoreCase(value) ? BAD_REQUEST :
                ValidatorUtils.getHttpStatusByCode(value);
    }

    @NotNull
    private ResponseEntity<Void> generateMockOTP() {
        return ResponseEntity.ok().build();
    }
}