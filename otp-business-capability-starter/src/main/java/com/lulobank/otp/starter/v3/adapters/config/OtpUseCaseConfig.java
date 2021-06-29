package com.lulobank.otp.starter.v3.adapters.config;

import com.lulobank.otp.services.v3.port.in.DeleteOTPCardlessWithdrawalPort;
import com.lulobank.otp.services.v3.port.in.zendesk.SacGenerateAuthorizationPort;
import com.lulobank.otp.services.v3.port.in.zendesk.SacVerifyAuthorizationPort;
import com.lulobank.otp.services.v3.port.out.clients.ClientsPort;
import com.lulobank.otp.services.v3.port.out.notifactions.NotifyService;
import com.lulobank.otp.services.v3.port.out.redis.HashRepositoryPort;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.services.v3.usecase.deleteotptoken.DeleteOTPCardlessWithdrawalUseCase;
import com.lulobank.otp.services.v3.usecase.sac.SacGenerateAuthorizationUseCase;
import com.lulobank.otp.services.v3.usecase.sac.SackVerifyAuthorizationUseCase;
import com.lulobank.otp.starter.v3.adapters.out.clients.config.ClientsServiceConfig;
import com.lulobank.otp.starter.v3.adapters.out.notifications.NotificationConfigV3;
import com.lulobank.otp.starter.v3.adapters.out.redis.RedisConfigV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RedisConfigV3.class, NotificationConfigV3.class, ClientsServiceConfig.class})
public class OtpUseCaseConfig {

    @Bean
    public SacGenerateAuthorizationPort getZendeskGenerateAuthorizationPort(ClientsPort clientsPort,
                                                                            KeyValRepository keyValRepository,
                                                                            NotifyService notifyService) {
        return new SacGenerateAuthorizationUseCase(clientsPort, keyValRepository, notifyService);
    }

    @Bean
    public SacVerifyAuthorizationPort getSacVerifyAuthorizationPort(KeyValRepository keyValRepository) {
        return new SackVerifyAuthorizationUseCase(keyValRepository);
    }

    @Bean
    public DeleteOTPCardlessWithdrawalPort getDeleteOTPCardlessWithdrawalPort(HashRepositoryPort hashRepositoryPort) {
        return new DeleteOTPCardlessWithdrawalUseCase(hashRepositoryPort);
    }
}
