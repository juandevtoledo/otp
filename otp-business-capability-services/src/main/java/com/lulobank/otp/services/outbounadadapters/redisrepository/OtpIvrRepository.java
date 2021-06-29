package com.lulobank.otp.services.outbounadadapters.redisrepository;

import com.lulobank.otp.services.outbounadadapters.model.OtpIvrRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface OtpIvrRepository extends CrudRepository<OtpIvrRedisEntity, String> {
}
