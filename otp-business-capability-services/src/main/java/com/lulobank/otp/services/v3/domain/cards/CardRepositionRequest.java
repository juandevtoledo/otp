package com.lulobank.otp.services.v3.domain.cards;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardRepositionRequest implements OTPTransactionData {
    @NotNull
    private String color;
    @NotNull
    private String nameOnCard;
    @Valid
    private CardHolderAddress cardHolder;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardHolderAddress implements OTPTransactionData {
        @NotNull
        private String address;
        @NotNull
        private String addressPrefix;
        @NotNull
        private String addressAdditionalInfo;
        private String zipCode;
        @NotNull
        private String city;
        @NotNull
        private String cityId;
        @NotNull
        private String department;
        @NotNull
        private String departmentId;
        @NotNull
        private String code;
    }

}
