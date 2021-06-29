package com.lulobank.otp.starter.config;

import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpIvrRepository;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@Profile("!test")
@EnableRedisRepositories(basePackageClasses = {OtpRepository.class, OtpIvrRepository.class})
public class RedisConfig {

    @Value("${spring.redis.host}")
    String host;
    @Value("${spring.redis.port}")
    int port;

    
}
