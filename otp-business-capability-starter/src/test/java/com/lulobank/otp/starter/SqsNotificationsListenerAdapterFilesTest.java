package com.lulobank.otp.starter;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.core.events.Event;
import com.lulobank.core.helpers.GlobalConstants;
import com.lulobank.otp.sdk.dto.events.EmailMessageNotification;
import com.lulobank.otp.services.inboundadapters.SqsNotificationsListenerAdapter;
import com.lulobank.otp.services.outbounadadapters.services.SESEmailSender;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.ports.service.FileService;
import com.lulobank.otp.services.utils.EventUtils;
import com.lulobank.otp.sdk.dto.email.EmailAttachment;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SqsNotificationsListenerAdapterFilesTest extends AbstractBaseIntegrationTest {
  private SqsNotificationsListenerAdapter testedClass;
  @Mock
  private SMSMessageSender smsMessageSender;
  @Mock
  private SESEmailSender sesEmailSender;
  @Mock
  private FileService fileService;
  private EmailTemplate emailTemplate;
  private String urlFile = "https://lulo-terminos-y-condiciones-sand.s3.amazonaws" + ".com/contrato-ahorros/Contrato+Lulo-c.pdf";

  private Map<String, Object> headers;


  @Override
  protected void init() {
    MockitoAnnotations.initMocks(this);
    testedClass = new SqsNotificationsListenerAdapter(smsMessageSender, sesEmailSender, pushNotificationService, fileService);
    emailTemplate = new EmailTemplate();
    emailTemplate.setFrom("mail@mail.com");
    emailTemplate.setBody(GlobalConstants.EMPTY_STRING);
    emailTemplate.setSubject(GlobalConstants.EMPTY_STRING);

    headers = new HashMap<>();
    headers.put("authorizationToken", "fakeToken");
  }

  @Ignore // Hacemos depender la prueba de una recurso externo, cuando el PDF no está en S3 la prueba falla
  @Test
  public void Raw_Email_Read_And_Sent_with_attachments_Text_URL_Files_Exception() throws IOException {
    EmailMessageNotification message = new EmailMessageNotification();
    EmailAttachment attachment1 = new EmailAttachment("", new byte[0], "application/pdf");
    attachment1.setReportUrl(urlFile);
    EmailAttachment attachment2 = new EmailAttachment("", new byte[0], "application/pdf");
    attachment2.setReportUrl(urlFile);
    emailTemplate.getFiles().add(attachment1);
    emailTemplate.getFiles().add(attachment2);
    doNothing().when(mailSender).sendEmail(any(EmailTemplate.class));
    message.setEmailTemplate(emailTemplate);
    Event<EmailMessageNotification> event = new EventUtils().getEvent(message);
    ObjectMapper objectMapper = new ObjectMapper();
    testedClass.getMessage(headers, objectMapper.writeValueAsString(event));
    verify(sesEmailSender, times(1)).sendEmail(any(EmailTemplate.class));
  }

  @Test
  public void Raw_Email_Read_and_Sent_with_attachments_S3() throws IOException {
    EmailMessageNotification messageNotification = new EmailMessageNotification();
    EmailAttachment attachment1 = new EmailAttachment("File1", new byte[0], "application/pdf");
    attachment1.setReportUrl("s3://00183d14-749c-4dc0-a92a-6747c35eca4a" +
            "/APP/terminoscondiciones/2020-07-13T00:20:27/terminoscondiciones.pdf");
    EmailAttachment attachment2 = new EmailAttachment("File2", new byte[0], "application/pdf");
    attachment2.setReportUrl("s3://00183d14-749c-4dc0-a92a-6747c35eca4a" +
            "/APP/politicaprivacidad/2020-07-13T00:20:27/politicaprivacidad.pdf");
    emailTemplate.getFiles().add(attachment1);
    emailTemplate.getFiles().add(attachment2);
    S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(
            IOUtils.toInputStream("123", StandardCharsets.UTF_8),
            mock(HttpRequestBase.class));
    when(fileService.getFile(any())).thenReturn(s3ObjectInputStream);
    doNothing().when(mailSender).sendEmail(any(EmailTemplate.class));
    messageNotification.setEmailTemplate(emailTemplate);
    Event<EmailMessageNotification> event = new EventUtils<>().getEvent(messageNotification);
    ObjectMapper objectMapper = new ObjectMapper();
    testedClass.getMessage(headers, objectMapper.writeValueAsString(event));
    verify(sesEmailSender, times(1)).sendEmail(any(EmailTemplate.class));
  }

}
