package com.lulobank.otp.services.v3.port.in.zendesk;

import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.domain.zendesk.VerifyAuthorizationSac;
import com.lulobank.otp.services.v3.util.UseCase;
import io.vavr.control.Either;

public interface SacVerifyAuthorizationPort extends UseCase<VerifyAuthorizationSac, Either<UseCaseResponseError, Boolean>> {
}
