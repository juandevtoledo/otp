package com.lulobank.otp.services.features.otp;

import com.lulobank.core.Handler;
import com.lulobank.core.Response;
import com.lulobank.otp.sdk.dto.VerifyOperationResponse;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.utils.OtpRedisEntityUtils;
import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.function.Predicate;

import static com.lulobank.core.utils.ValidatorUtils.getListValidations;
import static com.lulobank.otp.services.utils.ErrorsOTP.OTP_VALIDATION_ERROR;


public class VerifyOperationHandler implements Handler<Response<VerifyOperationResponse>, ValidateOtpForNewLoanCommand> {
    private static final Logger logger = LoggerFactory.getLogger(VerifyOperationHandler.class);
    private final OtpRepository otpRepository;

    public VerifyOperationHandler(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public Response<VerifyOperationResponse> handle(ValidateOtpForNewLoanCommand command) {
        logger.info("Start otp verify , idClient : {}",command.getIdClient());
        return Option.ofOptional(getOtpForNewLoan(command))
                .filter(otpMatcher(command))
                .peek(otpRepository::delete)
                .map(n -> new Response<>(new VerifyOperationResponse(true)))
                .getOrElse(() -> handleError(command.getIdClient()));
    }

    @NotNull
    private Predicate<OtpRedisEntity> otpMatcher(ValidateOtpForNewLoanCommand command) {
        return e -> {
            boolean result = false;
            if (command.getOtp().equals(e.getOtp()))
                result = true;
            else
                logger.warn("Otp not Matchers , otp Request " + command.getOtp());
            return result;
        };
    }

    @NotNull
    private Optional<OtpRedisEntity> getOtpForNewLoan(ValidateOtpForNewLoanCommand command) {
        return otpRepository.findById(OtpRedisEntityUtils.verifyOtpIdForNewLoan(
                command.getIdClient(),
                command.getIdCredit(),
                command.getIdOffer())
        );
    }

    @NotNull
    private Response<VerifyOperationResponse> handleError(String idClient) {
        logger.error(String.format("Exception: %s %s", OTP_VALIDATION_ERROR, idClient));
        return new Response<>(getListValidations(OTP_VALIDATION_ERROR, String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())));
    }
}
