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
import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CloseAccountLulo implements OTPTransactionData {
    @NotNull
    private String description;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String currency;
    @NotNull
    private TargetAccount target;
    @Valid
    @NotNull
    private DeviceFingerPrint deviceFingerPrint;
}
