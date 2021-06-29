package com.lulobank.otp.services.v3.usecase.sac;

import com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.domain.zendesk.TransactionTypeSac;
import com.lulobank.otp.services.v3.domain.zendesk.VerifyAuthorizationSac;
import com.lulobank.otp.services.v3.port.in.zendesk.SacVerifyAuthorizationPort;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;

@Slf4j
@RequiredArgsConstructor
public class SackVerifyAuthorizationUseCase implements SacVerifyAuthorizationPort {

    private static final String SEPARATOR = ":";

    private final KeyValRepository keyValRepository;

    @Override
    public Either<UseCaseResponseError, Boolean> execute(VerifyAuthorizationSac command) {
        return Option.of(command)
                .filter(this::isValidTransactionType)
                .map(this::verifyOtp)
                .getOrElse(() -> Either.left(new UseCaseResponseError(UseCaseErrorStatus.OTP_181.name(),
                        UseCaseErrorStatus.OTP_181.getMessage(), UseCaseErrorStatus.DEFAULT_DETAIL)));
    }

    private Either<UseCaseResponseError, Boolean> verifyOtp(VerifyAuthorizationSac verifyAuthorizationSac) {
        String key = buildKey(verifyAuthorizationSac);
        return keyValRepository.getByKey(key)
                .mapLeft(keyValRepositoryError -> (UseCaseResponseError) keyValRepositoryError)
                .flatMap(optOtp -> optOtp
                        .filter(otp -> verifyAuthorizationSac.getOtp().equals(otp))
                        .peek(otp -> keyValRepository.remove(key))
                        .map(otp -> true)
                        .toEither(new UseCaseResponseError(UseCaseErrorStatus.OTP_182.name(),
                                UseCaseErrorStatus.OTP_182.name(), UseCaseErrorStatus.DEFAULT_DETAIL)));
    }

    private boolean isValidTransactionType(VerifyAuthorizationSac verifyAuthorizationSac) {
        return EnumUtils.isValidEnum(TransactionTypeSac.class, verifyAuthorizationSac.getTransactionType());
    }

    private String buildKey(VerifyAuthorizationSac verifyAuthorizationSac) {
        return new StringBuilder()
                .append(verifyAuthorizationSac.getClientId())
                .append(SEPARATOR)
                .append(verifyAuthorizationSac.getAgentId())
                .append(SEPARATOR)
                .append(verifyAuthorizationSac.getTransactionType())
                .append(SEPARATOR)
                .append(verifyAuthorizationSac.getProductNumber())
                .toString();
    }


}
