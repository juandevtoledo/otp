package com.lulobank.otp.sdk.dto.external;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyWithdrawalOperation extends VerifyExternalOperation {
  private Withdrawal withdrawal;

  @Override
  public AbstractExternalOperation findOperation() {
    WithdrawalOperation withdrawalOperation = new WithdrawalOperation();
    withdrawalOperation.setOperationId("withdrawal");
    withdrawalOperation.setWithdrawal(this.withdrawal);
    return withdrawalOperation;
  }
}
