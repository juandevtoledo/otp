package com.lulobank.otp.services.features.onboardingotp.model;

import com.lulobank.core.Command;
import com.lulobank.otp.sdk.dto.AbstractCommandFeatures;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import lombok.Getter;

import java.util.Map;

@Getter
public class OtpEmailRequest extends AbstractCommandFeatures implements Command {
  private EmailTemplate emailTemplate;
  private String otp;

  public OtpEmailRequest(EmailTemplate emailTemplate, Map<String, String> httpHeaders) {
    super(httpHeaders);
    this.emailTemplate = emailTemplate;
  }

  public OtpEmailRequest(EmailTemplate emailTemplate, String otp, Map<String, String> httpHeaders) {
    super(httpHeaders);
    this.emailTemplate = emailTemplate;
    this.otp = otp;
  }
}
