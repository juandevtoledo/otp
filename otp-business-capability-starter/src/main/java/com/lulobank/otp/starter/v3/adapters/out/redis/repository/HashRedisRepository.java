package com.lulobank.otp.starter.v3.adapters.out.redis.repository;

import com.lulobank.otp.starter.v3.adapters.out.redis.model.OtpRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface HashRedisRepository extends CrudRepository<OtpRedisEntity, String> {
}
