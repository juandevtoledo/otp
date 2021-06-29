package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CloseAccountSimpleCeroBalance implements OTPTransactionData {
    @NotNull
    private String idSavingsAccount;
    @NotNull
    private String cancellationReason;
    @Valid
    @NotNull
    private DeviceFingerPrint deviceFingerPrint;
}