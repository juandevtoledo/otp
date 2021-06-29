package com.lulobank.otp.starter.v3.config;

import com.lulobank.otp.services.v3.port.in.GenerateOTP;
import com.lulobank.otp.services.v3.port.in.VerifyOTP;
import com.lulobank.otp.services.v3.port.out.notifactions.NotifyService;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.services.v3.services.DefaultKeyService;
import com.lulobank.otp.services.v3.services.OTPGenerator;
import com.lulobank.otp.services.v3.usecase.GenerateOTPUseCase;
import com.lulobank.otp.services.v3.usecase.VerifyOTPUseCase;
import com.lulobank.otp.starter.v3.adapters.out.notifications.NotificationConfigV3;
import com.lulobank.otp.starter.v3.adapters.out.redis.RedisConfigV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({RedisConfigV3.class, NotificationConfigV3.class})
public class GenericOTPConfig {


    @Bean
    public GenerateOTP generateOTP(KeyValRepository keyValRepository,
                                   NotifyService notifyService) {
        return new GenerateOTPUseCase(
                new DefaultKeyService(),
                keyValRepository,
                notifyService, new OTPGenerator());
    }

    @Bean
    public VerifyOTP verifyOTP(KeyValRepository keyValRepository) {
        return new VerifyOTPUseCase(new DefaultKeyService(), keyValRepository);
    }


}
