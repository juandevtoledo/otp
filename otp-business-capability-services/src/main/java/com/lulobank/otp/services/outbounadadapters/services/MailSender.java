package com.lulobank.otp.services.outbounadadapters.services;

import com.lulobank.otp.sdk.dto.email.EmailTemplate;

public interface MailSender {

  void sendEmail(EmailTemplate emailTemplate);

}

