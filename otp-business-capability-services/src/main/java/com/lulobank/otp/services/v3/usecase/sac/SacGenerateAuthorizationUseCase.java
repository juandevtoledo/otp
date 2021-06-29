package com.lulobank.otp.services.v3.usecase.sac;

import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.domain.error.GeneralErrorStatus;
import com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.domain.zendesk.AuthorizationSac;
import com.lulobank.otp.services.v3.domain.zendesk.TransactionTypeSac;
import com.lulobank.otp.services.v3.port.in.zendesk.SacGenerateAuthorizationPort;
import com.lulobank.otp.services.v3.port.out.clients.ClientsPort;
import com.lulobank.otp.services.v3.port.out.notifactions.NotifyService;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import io.vavr.API;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;

import static io.vavr.API.$;
import static io.vavr.API.Case;

@Slf4j
@RequiredArgsConstructor
public class SacGenerateAuthorizationUseCase implements SacGenerateAuthorizationPort {

    private static final String SEPARATOR = ":";

    private final ClientsPort clientsPort;
    private final KeyValRepository keyValRepository;
    private final NotifyService notifyService;

    @Override
    public Either<UseCaseResponseError, Boolean> execute(AuthorizationSac command) {
        return Option.of(command)
                .filter(this::isValidTransactionType)
                .map(authorizationSac ->
                    clientsPort.getClientNotificationInfoSac(command.getAuthorizationHeader(), command.getClientId())
                    .mapLeft(clientsServiceError -> (UseCaseResponseError) clientsServiceError)
                    .flatMap(clientNotifyInfo ->  createAuthorizationOtp(command,clientNotifyInfo))
                ).getOrElse(() -> Either.left(new UseCaseResponseError(UseCaseErrorStatus.OTP_181.name(),
                        UseCaseErrorStatus.OTP_181.getMessage(), GeneralErrorStatus.DEFAULT_DETAIL)));
    }

    private Either<UseCaseResponseError, Boolean> createAuthorizationOtp(AuthorizationSac command, ClientNotifyInfo clientNotifyInfo) {
            return keyValRepository.save(createKey(command),OtpService.getInstance().create())
                    .mapLeft(keyValRepositoryError -> (UseCaseResponseError) keyValRepositoryError)
                    .peek(otp -> sentOtpEmailNotification(otp,clientNotifyInfo,command))
                    .map(otp -> true);
    }

    private void sentOtpEmailNotification(String otp, ClientNotifyInfo clientNotifyInfo, AuthorizationSac command) {
        notifyService.notifyAuthorizationSac(otp,clientNotifyInfo,getMessageAuthorization(command));
    }

    private String getMessageAuthorization(AuthorizationSac command) {
        return API.Match(command.getTransactionType()).of(
                Case($(TransactionTypeSac.LOCK_ACCOUNT_SAC.name()), TransactionTypeSac.LOCK_ACCOUNT_SAC::name),
                Case($(TransactionTypeSac.FREEZE_CARD_SAC.name()), TransactionTypeSac.FREEZE_CARD_SAC::name));
    }

    private String createKey(AuthorizationSac authorizationSac) {
        return new StringBuilder()
                .append(authorizationSac.getClientId())
                .append(SEPARATOR)
                .append(authorizationSac.getAgentId())
                .append(SEPARATOR)
                .append(authorizationSac.getTransactionType())
                .append(SEPARATOR)
                .append(authorizationSac.getProductNumber())
                .toString();
    }

    private boolean isValidTransactionType(AuthorizationSac authorizationSac) {
        return EnumUtils.isValidEnum(TransactionTypeSac.class,authorizationSac.getTransactionType());
    }


}
