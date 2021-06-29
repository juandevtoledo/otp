package com.lulobank.otp.services.v3.domain.accountclosure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class CloseAccountInterbank implements OTPTransactionData {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal gmf;
    @NotNull
    private String currency;
    @NotNull
    private String reason;
    @NotNull
    private TransferTarget target;
    @Valid
    @NotNull
    private DeviceFingerPrint deviceFingerPrint;

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransferTarget implements OTPTransactionData {
        @NotNull
        private String documentId;
        @NotNull
        private String documentType;
        @NotNull
        private String name;
        @NotNull
        private String phone;
        @NotNull
        private String prefix;
        @NotNull
        private BankAccount bankAccount;
    }
}