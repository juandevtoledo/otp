package com.lulobank.otp.starter.v3.adapters.out.notifications;

import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class NotifyServiceMail {

    private MailSender sesEmailSender;

    @Value("${mail.sender}")
    private String emailSenderAddress;

    @Value("${mail.bcc}")
    private String emailBcc;

    @Value("${mail.otp-email.email-subject}")
    private String otpEmailSubject;


    @Autowired
    public NotifyServiceMail(MailSender sesEmailSender) {
        this.sesEmailSender = sesEmailSender;
    }

    public Try<Boolean> sendMessage(String message, String email, String otp) {
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setSubject(otpEmailSubject);
        emailTemplate.setFrom(emailSenderAddress);
        emailTemplate.setBcc(Arrays.asList(emailBcc));
        emailTemplate.setBody(String.format(message, otp));
        emailTemplate.setTo(Collections.singletonList(email));
        return Try.of(() -> {
            sesEmailSender.sendEmail(emailTemplate);
            return true;
        });
    }

}
