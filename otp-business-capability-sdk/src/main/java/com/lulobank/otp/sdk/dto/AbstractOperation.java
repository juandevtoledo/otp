package com.lulobank.otp.sdk.dto;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AbstractOperation extends AbstractCommandFeatures implements Command {
  private String id;
  private String idClient;
}
