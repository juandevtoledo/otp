package com.lulobank.otp.services.outbounadadapters.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@RedisHash("otp")
public class OtpRedisEntity {
  @Id
  private String id;
  private String otp;
  @TimeToLive
  private int expiration;

  public OtpRedisEntity(String id, String otp, int expiration) {
    this.id = id;
    this.otp = otp;
    this.expiration = expiration;
  }
}
