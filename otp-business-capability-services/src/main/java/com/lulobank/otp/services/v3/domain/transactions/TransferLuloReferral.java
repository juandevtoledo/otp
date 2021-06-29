package com.lulobank.otp.services.v3.domain.transactions;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferLuloReferral implements OTPTransactionData {
    @Valid
    @NotNull
    private TransferSource source;
    @Valid
    @NotNull
    private TransferTarget target;
    @Valid
    @NotNull
    private DeviceFingerPrint deviceFingerPrint;
    @NotNull
    private Double amount;
    @NotNull
    private String currency;
    @NotNull
    private String shippingReason;
}
