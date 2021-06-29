package com.lulobank.otp.services.v3.domain.cards;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardActivation implements OTPTransactionData {
    @NotNull
    private String newPin;
}
