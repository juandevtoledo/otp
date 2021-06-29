package com.lulobank.otp.sdk.dto.external;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class VerifyExternalOperation implements Command {
  private String otp;

  public abstract AbstractExternalOperation findOperation();
}
