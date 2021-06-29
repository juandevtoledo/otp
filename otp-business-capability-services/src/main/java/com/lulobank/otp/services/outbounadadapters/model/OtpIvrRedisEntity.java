package com.lulobank.otp.services.outbounadadapters.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@RedisHash("otp")
public class OtpIvrRedisEntity {
    @Id
    private String id;
    private String otp;
    private String productNumber;
    @TimeToLive
    private int expiration;

    public OtpIvrRedisEntity(String id, String otp,
                             int expiration, String productNumber) {
        this.id = id;
        this.otp = otp;
        this.expiration = expiration;
        this.productNumber = productNumber;
    }
}
