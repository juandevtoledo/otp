package com.lulobank.otp.sdk.dto.external;

import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractExternalOperation implements Command {
  private String operationId;
}
