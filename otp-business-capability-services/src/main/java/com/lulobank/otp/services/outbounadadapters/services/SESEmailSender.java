package com.lulobank.otp.services.outbounadadapters.services;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.UUID;

import static io.vavr.API.*;

@Slf4j
public class SESEmailSender implements MailSender {

    private AmazonSimpleEmailService sesClient;

    private EmailTemplate emailTemplate;

    public SESEmailSender(AmazonSimpleEmailService amazonSimpleEmailService) {
        this.sesClient = amazonSimpleEmailService;
    }

    public void sendEmail(EmailTemplate emailTemplate) {
        log.info("Send OTP Email to {}", emailTemplate.getTo());
        this.emailTemplate = emailTemplate;
        Try.run(() -> Match(emailTemplate.getFiles().isEmpty()).of(
                Case($(true), e -> sendPlainEmail()),
                Case($(false), y -> sendRawEmail(emailTemplate))
        )).onFailure(Exception.class, ex -> log.error("Exception while sending SES email", ex));

    }

    private boolean sendRawEmail(EmailTemplate emailTemplate) {

        Try.run(() -> {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);
            message.setSubject(emailTemplate.getSubject(), "UTF-8");
            message.setFrom(new InternetAddress(emailTemplate.getFrom()));
            message.setReplyTo(AddressFactory.of(emailTemplate.getFrom()));

            message.setRecipients(MimeMessage.RecipientType.TO, AddressFactory.of(emailTemplate.getTo()));
            message.setRecipients(MimeMessage.RecipientType.CC, AddressFactory.of(emailTemplate.getCc()));
            message.setRecipients(MimeMessage.RecipientType.BCC, AddressFactory.of(emailTemplate.getBcc()));

            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(createBody(emailTemplate));
            mapAttachments(emailTemplate, mp);

            message.setContent(mp);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            message.writeTo(outputStream);
            RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
            SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
            sesClient.sendRawEmail(rawEmailRequest);
        }).onFailure(ex -> log.error("Error in SES raw email", ex));

        return true;
    }

    private void mapAttachments(EmailTemplate emailTemplate, MimeMultipart mp) {
        emailTemplate.getFiles().forEach(
                e -> Try.run(() -> {
                    MimeBodyPart attachment = new MimeBodyPart();
                    DataSource ds = new ByteArrayDataSource(e.getContent(), e.getContentType());
                    attachment.setDataHandler(new DataHandler(ds));
                    attachment.setHeader("Content-ID", "<" + UUID.randomUUID().toString() + ">");
                    attachment.setFileName(e.getName());
                    mp.addBodyPart(attachment);
                }).onFailure(ex -> log.error("Error in SES map attachments", ex))
        );
    }

    private BodyPart createBody(EmailTemplate emailTemplate) throws MessagingException {
        BodyPart part = new MimeBodyPart();
        if (emailTemplate.isHtml()) {
            part.setContent(emailTemplate.getBody(), "text/html");
        } else {
            part.setText(emailTemplate.getBody());
        }
        return part;
    }


    private boolean sendPlainEmail() {
        Destination destination = new Destination().withToAddresses(emailTemplate.getTo());
        destination.withCcAddresses(emailTemplate.getCc());
        destination.withBccAddresses(emailTemplate.getBcc());
        Content subject = new Content().withData(emailTemplate.getSubject());
        Content textBody = new Content().withData(emailTemplate.getBody());
        Body body = emailTemplate.isHtml() ? new Body().withHtml(textBody) : new Body().withText(textBody);
        Message message = new Message().withSubject(subject).withBody(body);
        SendEmailRequest request = new SendEmailRequest().withSource(emailTemplate.getFrom())
                .withReplyToAddresses(emailTemplate.getFrom()).withDestination(destination)
                .withMessage(message);
        sesClient.sendEmail(request);
        return true;
    }
}
