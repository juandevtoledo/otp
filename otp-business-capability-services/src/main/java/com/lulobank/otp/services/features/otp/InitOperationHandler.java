package com.lulobank.otp.services.features.otp;

import com.lulobank.clients.sdk.operations.dto.ClientInformationByIdClient;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.otp.sdk.dto.OperationResponse;
import com.lulobank.otp.services.actions.OtpOperationMessage;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.utils.OtpRedisEntityUtils;
import com.lulobank.utils.exception.ServiceException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import java.security.InvalidKeyException;
import java.util.Map;
import java.util.Optional;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static com.lulobank.otp.services.utils.ErrorsOTP.OTP_GENERATION_ERROR;
import static com.lulobank.otp.services.utils.ErrorsOTP.OTP_VALIDATION_ERROR;
import static com.lulobank.otp.services.utils.ErrorsOTP.SERVICE_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class InitOperationHandler implements Handler<Response<OperationResponse>, GenerateOtpForNewLoanCommand> {


    private static final String ID_OPERATION = "hire-loan";

    private final OtpRepository otpRepository;
    private final Map<String, OtpOperationMessage> operationMessageMap;
    private final OtpService otpService;
    private final RetrofitClientOperations retrofitClientOperations;

    public InitOperationHandler(OtpRepository otpRepository, Map<String, OtpOperationMessage> operationMessages,
                                OtpService otpService, RetrofitClientOperations retrofitClientOperations) {
        this.otpRepository = otpRepository;
        this.operationMessageMap = operationMessages;
        this.otpService = otpService;
        this.retrofitClientOperations = retrofitClientOperations;
    }

    @Override
    public Response<OperationResponse> handle(GenerateOtpForNewLoanCommand command) {
        log.info("Start Otp - Hire Credit, idClient : {} ",command.getIdClient());
        return Try.of(() -> getOperationResponse(command))
                .recover(ServiceException.class, this::handleException)
                .recover(InvalidKeyException.class, this::handleException)
                .get();
    }

    @NotNull
    private Response<OperationResponse> getOperationResponse(GenerateOtpForNewLoanCommand command) {
        return Try.of(() -> otpService.create(getOptLength()))
                .peek(otpNumber -> otpRepository.save(getOtpRedisEntity(command, otpNumber)))
                .map(otpNumber -> createOperationResponse(otpNumber, command))
                .peek(otp->log.info("Otp - Hire Credit, saved : {} ",command.getIdClient()))
                .get();
    }

    private int getOptLength() {
        return Option.of(operationMessageMap.get(ID_OPERATION))
                .map(OtpOperationMessage::getLength)
                .get();
    }

    private Response<OperationResponse> handleError(String errorMessage, HttpStatus status) {
        return new Response<>(getListValidations(errorMessage, String.valueOf(status.value())));
    }

    private Response<OperationResponse> handleException(Throwable throwable) {
        log.error(OTP_GENERATION_ERROR, throwable);
        return handleError(SERVICE_ERROR, INTERNAL_SERVER_ERROR);
    }

    private OtpRedisEntity getOtpRedisEntity(GenerateOtpForNewLoanCommand command, String otpNumber) {
        return new OtpRedisEntity(
                OtpRedisEntityUtils.generateOtpIdForNewLoan(
                        command.getIdClient(),
                        command.getIdCredit(),
                        command.getIdOffer()),
                otpNumber,
                OtpRedisEntityUtils.OPERATION_EXPIRATION_TIME);
    }

    private String getOperationMessage(String otpNUmber) {
        return Optional.ofNullable(this.operationMessageMap.get(ID_OPERATION))
                .map(OtpOperationMessage::getMessage)
                .map(message -> message + otpNUmber)
                .orElse(StringUtils.EMPTY);
    }

    private Response<OperationResponse> createOperationResponse(String otpNumber, GenerateOtpForNewLoanCommand command) {
        return Option.of(getClientInfo(command))
                .map(clientInfo -> {
                    OperationResponse response = new OperationResponse();
                    response.setChallenge(otpNumber);
                    response.setPhone(clientInfo.getContent().getPhoneNumber());
                    response.setPrefix(String.valueOf(clientInfo.getContent().getPhonePrefix()));
                    response.setMessage(getOperationMessage(otpNumber));
                    return new Response<>(response);
                }).getOrElse(handleError(OTP_VALIDATION_ERROR, NOT_ACCEPTABLE));
    }

    private ClientInformationByIdClient getClientInfo(GenerateOtpForNewLoanCommand command) {
        return retrofitClientOperations.getClientByIdClient(command.getAuthorizationHeader(), command.getIdClient());
    }

}
