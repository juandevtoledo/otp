package com.lulobank.otp.sdk.operations;

import com.lulobank.otp.sdk.dto.external.ExternalOperationResponse;
import com.lulobank.otp.sdk.dto.external.WithdrawalOperation;

import java.util.Map;

public interface IOtpCardlessWithdrawal {
  ExternalOperationResponse generateOtpCardlessWithdrawal(Map<String, String> headers, WithdrawalOperation withdrawalOperation);
}
