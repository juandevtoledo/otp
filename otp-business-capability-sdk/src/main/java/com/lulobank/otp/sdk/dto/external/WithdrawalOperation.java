package com.lulobank.otp.sdk.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawalOperation extends AbstractExternalOperation {
  private Withdrawal withdrawal;
}
