package com.lulobank.otp.starter.v3.adapters.in.deleteotptoken.mapper;

import com.lulobank.otp.services.v3.domain.deleteotptoken.DeleteOTPCardlessWithdrawalRequest;
import com.lulobank.otp.services.v3.domain.deleteotptoken.WithdrawalOperation;
import com.lulobank.otp.starter.v3.adapters.in.deleteotptoken.dto.DeleteOTPCardlessWithdrawalInboundRequest;

import java.util.function.Function;

public final class DeleteOTPCardlessWithdrawalMapper {
    private DeleteOTPCardlessWithdrawalMapper() {
    }

    public static final Function<DeleteOTPCardlessWithdrawalInboundRequest, DeleteOTPCardlessWithdrawalRequest> toDeleteOTPTokenRequest = deleteOTPCardlessWithdrawalInboundRequest ->
            DeleteOTPCardlessWithdrawalRequest.builder()
                    .otp(deleteOTPCardlessWithdrawalInboundRequest.getOtp())
                    .withdrawalOperation(WithdrawalOperation.builder()
                            .withdrawal(WithdrawalOperation.Withdrawal.builder()
                                    .amount(deleteOTPCardlessWithdrawalInboundRequest.getWithdrawal().getAmount())
                                    .documentId(deleteOTPCardlessWithdrawalInboundRequest.getWithdrawal().getDocumentId())
                                    .build())
                            .build())
                    .build();
}
