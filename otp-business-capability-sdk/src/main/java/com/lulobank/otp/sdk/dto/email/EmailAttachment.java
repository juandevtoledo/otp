package com.lulobank.otp.sdk.dto.email;

public class EmailAttachment {
  private String name;
  private byte[] content;
  private String contentType;
  private String reportUrl;

  public EmailAttachment() {
  }

  public EmailAttachment(String name, byte[] content, String contentType) {
    this.name = name;
    this.content = content.clone();
    this.contentType = contentType;
  }

  public String getName() {
    return name;
  }

  public byte[] getContent() {
    return content.clone();
  }

  public String getContentType() {
    return contentType;
  }

  public String getReportUrl() {
    return reportUrl;
  }

  public void setReportUrl(String reportUrl) {
    this.reportUrl = reportUrl;
  }
}
