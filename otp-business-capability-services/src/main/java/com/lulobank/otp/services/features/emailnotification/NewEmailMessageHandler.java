package com.lulobank.otp.services.features.emailnotification;

import com.lulobank.core.events.Event;
import com.lulobank.core.events.EventHandler;
import com.lulobank.otp.sdk.dto.email.EmailAttachment;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import com.lulobank.otp.sdk.dto.events.EmailMessageNotification;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import com.lulobank.otp.services.ports.service.FileService;
import com.lulobank.otp.services.utils.LogMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NewEmailMessageHandler implements EventHandler<Event<EmailMessageNotification>> {
  private static final Logger LOGGER = LoggerFactory.getLogger(NewEmailMessageHandler.class);
  private MailSender mailSender;
  private String emailSenderAddress;
  private String emailBcc;
  private final FileService fileService;


  public NewEmailMessageHandler(MailSender mailSender, String emailSenderAddress,
                                String emailBcc, FileService fileService) {
    this.mailSender = mailSender;
    this.emailSenderAddress = emailSenderAddress;
    this.emailBcc = emailBcc;
    this.fileService = fileService;
  }

  @Override
  public void apply(Event<EmailMessageNotification> event) {
    EmailMessageNotification emailMessageNotification = event.getPayload();
    emailMessageNotification.getEmailTemplate().setFrom(emailSenderAddress);
    emailMessageNotification.getEmailTemplate().setBcc(Arrays.asList(emailBcc));
    try {
      emailMessageNotification.setEmailTemplate(validateEmailAttachments(emailMessageNotification.getEmailTemplate()));
      mailSender.sendEmail(emailMessageNotification.getEmailTemplate());
      LOGGER.trace(LogMessages.SENDING_MESSAGE_TO.name(), emailMessageNotification.getEmailTemplate().getTo());
    } catch (IOException ex) {
      LOGGER.error(LogMessages.SENDING_MESSAGE_TO.name(), emailMessageNotification.getEmailTemplate().getTo(), ex);
    }
  }

  public EmailTemplate validateEmailAttachments(EmailTemplate emailTemplate) throws IOException {
    if (Objects.nonNull(emailTemplate.getFiles()) && !emailTemplate.getFiles().isEmpty()) {
      List<EmailAttachment> emailAttachmentList = emailTemplate.getFiles().stream()
        .filter(emailAttachment -> emailAttachment.getReportUrl() != null)
        .collect(Collectors.toList());
      List<EmailAttachment> emailAttachmentNew = new ArrayList<>();
      if (!emailAttachmentList.isEmpty()) {
        for (EmailAttachment attachment : emailAttachmentList) {
           emailAttachmentNew.add(validateAttachment(attachment));
        }
      }
      emailAttachmentNew
        .addAll(emailTemplate.getFiles().stream().filter(emailAttachment -> emailAttachment.getReportUrl() == null)
          .collect(Collectors.toList()));
      emailTemplate.setFiles(emailAttachmentNew);
    }
    return emailTemplate;
  }

  public EmailAttachment validateAttachment(EmailAttachment emailAttachment) throws IOException {
    return new EmailAttachment(emailAttachment.getName(),
            emailAttachment.getReportUrl().startsWith("s3://")?
                    getFileS3AsByteArray(emailAttachment.getReportUrl()
                            .replace("s3://", "")):
            getFile(emailAttachment.getReportUrl()),
            emailAttachment.getContentType());
  }

  public byte[] getFile(String url) throws IOException {
    URL urlFile = new URL(url);
    return getFileAsByteArray(urlFile);
  }

  public byte[] getFileAsByteArray(URL url) throws IOException {
    URLConnection connection = url.openConnection();
    InputStream in = connection.getInputStream();
    int contentLength = connection.getContentLength();
    ByteArrayOutputStream tmpOut;
    if (contentLength != -1) {
      tmpOut = new ByteArrayOutputStream(contentLength);
    } else {
      tmpOut = new ByteArrayOutputStream(163840);
    }
    return getByteArray(in, tmpOut);
  }

  public byte[] getFileS3AsByteArray(String url) throws IOException {
    InputStream in = fileService.getFile(url);
    ByteArrayOutputStream tmpOut = new ByteArrayOutputStream(163840);
    return getByteArray(in, tmpOut);
  }

  public byte[] getByteArray(InputStream in, ByteArrayOutputStream tmpOut) throws IOException {
    byte[] buf = new byte[512];
    while (true) {
      int len = in.read(buf);
      if (len == -1) {
        break;
      }
      tmpOut.write(buf, 0, len);
    }
    in.close();
    tmpOut.close();
    return tmpOut.toByteArray();
  }

}
