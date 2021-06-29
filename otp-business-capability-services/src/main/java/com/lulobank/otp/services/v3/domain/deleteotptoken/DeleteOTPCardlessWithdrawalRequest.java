package com.lulobank.otp.services.v3.domain.deleteotptoken;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeleteOTPCardlessWithdrawalRequest {
    private String otp;
    private WithdrawalOperation withdrawalOperation;

}
