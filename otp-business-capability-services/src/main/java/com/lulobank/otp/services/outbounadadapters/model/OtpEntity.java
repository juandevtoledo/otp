package com.lulobank.otp.services.outbounadadapters.model;

import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpEntity {
  private String prefix;
  private String phoneNumber;
  private EmailTemplate emailTemplate;
  private String otp;

  public OtpEntity(String prefix, String phoneNumber, String otp) {
    this.prefix = prefix;
    this.phoneNumber = phoneNumber;
    this.otp = otp;
  }

  public OtpEntity(EmailTemplate emailTemplate, String otp) {
    this.emailTemplate = emailTemplate;
    this.otp = otp;
  }
}
