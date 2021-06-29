package com.lulobank.otp.starter.v3.adapters.out.redis;

import com.lulobank.otp.services.v3.port.out.redis.HashRepositoryPort;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.starter.v3.adapters.out.redis.repository.HashRedisRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@Profile("!test")
@EnableRedisRepositories(basePackageClasses = {HashRedisRepository.class})
public class RedisConfigV3 {

    @Bean
    public KeyValRepository keyValRepository(StringRedisTemplate stringRedisTemplate) {
        return new KeyValRepositoryImpl(stringRedisTemplate);
    }

    @Bean
    public HashRepositoryPort hashRepositoryPort(HashRedisRepository hashRedisRepository) {
        return new HashRepositoryAdapter(hashRedisRepository);
    }
}
