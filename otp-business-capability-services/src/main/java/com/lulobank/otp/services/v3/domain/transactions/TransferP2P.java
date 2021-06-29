package com.lulobank.otp.services.v3.domain.transactions;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TransferP2P implements OTPTransactionData {
    @NotNull
    private Double amount;
    @NotNull
    private String currency;
    @NotNull
    private String shippingReason;
    @Valid
    @NotNull
    private TransferSource source;
    @Valid
    @NotNull
    private TransferTarget target;
    @Valid
    @NotNull
    private DeviceFingerPrint deviceFingerPrint;
    @JsonProperty("geoLocation")
    private Geolocation geolocation;
}
