package com.lulobank.otp.starter.config;

import com.lulobank.core.security.spring.WebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!dev")
@Configuration
public class JWTGenerationSecurityConfig {

    @Bean
    public WebSecurity webSecurity() {
        return new WebSecurity();
    }
}
