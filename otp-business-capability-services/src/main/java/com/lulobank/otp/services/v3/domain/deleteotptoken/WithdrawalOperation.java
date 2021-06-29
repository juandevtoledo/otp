package com.lulobank.otp.services.v3.domain.deleteotptoken;


import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@Builder
public class WithdrawalOperation extends AbstractExternalOperation {
    private Withdrawal withdrawal;

    @Getter
    @Builder
    public static class Withdrawal {
        private BigInteger amount;
        private String documentId;
    }
}
