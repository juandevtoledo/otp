package com.lulobank.otp.sdk.dto.email;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class EmailTemplate {

    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String from;
    private String subject;
    private String body;
    private boolean html;
    private List<EmailAttachment> files;

    public EmailTemplate() {
        this.to = new ArrayList<>();
        this.cc = new ArrayList<>();
        this.bcc = new ArrayList<>();
        this.html = false;
        this.files = new ArrayList<>();
    }

}
