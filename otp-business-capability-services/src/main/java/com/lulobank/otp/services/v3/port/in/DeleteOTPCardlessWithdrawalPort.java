package com.lulobank.otp.services.v3.port.in;

import com.lulobank.otp.services.v3.domain.deleteotptoken.DeleteOTPCardlessWithdrawalRequest;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.util.UseCase;
import io.vavr.control.Either;

public interface DeleteOTPCardlessWithdrawalPort extends UseCase<DeleteOTPCardlessWithdrawalRequest, Either<UseCaseResponseError, Boolean>> {
}
