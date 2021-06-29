package com.lulobank.otp.services.v3.domain.transactions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferInterbank implements OTPTransactionData {

    @NotNull
    private BigDecimal amount;
    @NotBlank
    private String currency;
    @NotBlank
    private String shippingReason;
    @JsonProperty("geoLocation")
    private Geolocation geolocation;
    @Valid
    @NotNull
    private InterbankTransferSource source;
    @Valid
    @NotNull
    private InterbankTransferTarget target;
    @Valid
    @NotNull
    private DeviceFingerPrint deviceFingerPrint;

    @Data
    public static class InterbankTransferSource {
        @NotNull
        private Integer prefix;
        private String phone;
        private String name;
        @NotBlank
        private String sourceAccount;
        @NotBlank
        private String sourceAccountHolder;
    }

    @Data
    public static class InterbankTransferTarget {
        @NotBlank
        private String prefix;
        @NotBlank
        private String phone;
        @NotBlank
        private String name;
        @NotBlank
        private String documentId;
        @NotBlank
        private String documentType;
        @Valid
        @NotNull
        private ExternalBankAccount externalBankAccount;
    }

    @Data
    public static class ExternalBankAccount {
        @NotBlank
        private String bankId;
        @NotBlank
        private String bankDescription;
        @NotBlank
        private String account;
        @NotBlank
        private String accountType;
        private boolean accountEnrollment;
    }
}
