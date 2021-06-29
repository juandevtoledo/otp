package com.lulobank.otp.sdk.dto;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyAbstractOperation implements Command {
  private String otp;
  private AbstractOperation operation;
}
