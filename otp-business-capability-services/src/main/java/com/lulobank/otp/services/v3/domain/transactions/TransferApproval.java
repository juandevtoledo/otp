package com.lulobank.otp.services.v3.domain.transactions;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public class TransferApproval implements OTPTransactionData {
    @NotNull
    @Valid
    private PendingTransfer transfer;
    @NotNull
    @Valid
    private DeviceFingerPrint deviceFingerPrint;

    @Getter
    @Setter
    @EqualsAndHashCode
    public static class PendingTransfer {
        @NotBlank
        private String phoneNumber;
        @NotBlank
        private String originName;
        @NotNull
        private Double amount;
        @NotBlank
        private String currency;
        @NotBlank
        private String dueOn;
        @NotBlank
        private String transferId;
        @NotBlank
        private String transferType;
        @NotBlank
        private String channel;
        private String targetAccount;
    }
}
