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
public class TransferLulo implements OTPTransactionData {

    @NotNull
    private BigDecimal amount;
    @NotBlank
    private String currency;
    @NotBlank
    private String shippingReason;
    @Valid
    @NotNull
    private TransferSource source;
    @Valid
    @NotNull
    private TargetAccount target;
    @Valid
    @NotNull
    private DeviceFingerPrint deviceFingerPrint;
    @JsonProperty("geoLocation")
    private Geolocation geolocation;

    @Data
    public static class TargetAccount {
        @JsonProperty("targetAccount")
        @NotBlank
        private String account;
        @JsonProperty("targetAccountHolder")
        @NotBlank
        private String holder;
        @NotNull
        private Integer prefix;
        @NotBlank
        private String phone;
        private String name;
    }

}
