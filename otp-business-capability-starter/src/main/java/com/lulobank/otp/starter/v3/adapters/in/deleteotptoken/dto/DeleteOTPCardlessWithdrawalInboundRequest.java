package com.lulobank.otp.starter.v3.adapters.in.deleteotptoken.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Getter
@Setter
public class DeleteOTPCardlessWithdrawalInboundRequest {
    @NotBlank(message = "otp is null or empty")
    private String otp;
    @Valid
    @NotNull(message = "withdrawal is null")
    private WithdrawalRequest withdrawal;

    @Getter
    @Setter
    public static class WithdrawalRequest {
        @NotNull(message = "amount is null or empty")
        private BigInteger amount;
        @NotBlank(message = "documentId is null or empty")
        private String documentId;
    }
}
