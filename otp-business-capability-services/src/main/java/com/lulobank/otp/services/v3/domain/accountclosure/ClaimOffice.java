package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ClaimOffice implements OTPTransactionData {
    @NotNull
    private City city;
    @NotNull
    private Office office;

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class City implements OTPTransactionData {
        @NotNull
        private String code;
        @NotNull
        private String description;
        @NotNull
        private String zoneCode;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Office implements OTPTransactionData {
        @NotNull
        private String code;
        @NotNull
        private String description;
        @NotNull
        private String address;
    }
}
