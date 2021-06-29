package com.lulobank.otp.services.v3.usecase.deleteotptoken;

import com.lulobank.otp.services.v3.domain.deleteotptoken.DeleteOTPCardlessWithdrawalRequest;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.port.in.DeleteOTPCardlessWithdrawalPort;
import com.lulobank.otp.services.v3.port.out.redis.HashRepositoryPort;
import com.lulobank.otp.services.v3.util.HttpDomainStatus;
import com.lulobank.otp.services.v3.util.OperationUtils;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;

import static com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus.OTP_184;

@RequiredArgsConstructor
public class DeleteOTPCardlessWithdrawalUseCase implements DeleteOTPCardlessWithdrawalPort {

    private static final String OPERATION_ID = "withdrawal";

    private final HashRepositoryPort hashRepositoryPort;

    @Override
    public Either<UseCaseResponseError, Boolean> execute(DeleteOTPCardlessWithdrawalRequest deleteOTPCardlessWithdrawalRequest) {
        deleteOTPCardlessWithdrawalRequest.getWithdrawalOperation().setOperationId(OPERATION_ID);
        return OperationUtils.getOperationHash(deleteOTPCardlessWithdrawalRequest.getWithdrawalOperation())
                .flatMap(haskKey -> findOtp(haskKey, deleteOTPCardlessWithdrawalRequest.getOtp()));
    }

    private Either<UseCaseResponseError, Boolean> findOtp(String hashKey, String otp) {
        return hashRepositoryPort.findOTPById(hashKey)
                .mapLeft(keyValRepositoryError -> (UseCaseResponseError) keyValRepositoryError)
                .flatMap(value -> validateOTP(value, otp))
                .flatMap(valid -> deleteOtpFromRedis(hashKey));
    }

    private Either<UseCaseResponseError, Boolean> validateOTP(String otpStored, String otpRequested) {
        return Option.of(otpStored)
                .filter(otp -> otp.equals(otpRequested))
                .fold(() -> Either.left(new UseCaseResponseError(OTP_184.name(),
                        String.valueOf(HttpDomainStatus.PRECONDITION_FAILED.value()))), otp -> Either.right(true));
    }

    private Either<UseCaseResponseError, Boolean> deleteOtpFromRedis(String hashKey) {
        return hashRepositoryPort.deleteOTPById(hashKey)
                .mapLeft(keyValRepositoryError -> keyValRepositoryError);
    }
}
