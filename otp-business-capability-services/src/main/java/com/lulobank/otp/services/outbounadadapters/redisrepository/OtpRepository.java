package com.lulobank.otp.services.outbounadadapters.redisrepository;

import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface OtpRepository extends CrudRepository<OtpRedisEntity, String> {
}
