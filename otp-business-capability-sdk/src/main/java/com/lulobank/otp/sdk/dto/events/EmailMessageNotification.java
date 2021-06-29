package com.lulobank.otp.sdk.dto.events;

import com.lulobank.core.Command;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessageNotification implements Command {
  private String idClient;
  private EmailTemplate emailTemplate;
}
