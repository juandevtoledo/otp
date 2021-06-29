package com.lulobank.otp.services.v3.domain.transactions;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credit implements OTPTransactionData {
    @NotNull
    private String target;
    @NotNull
    private Double amount;
    @NotNull
    private String offer;
    @NotNull
    private int creditTerm;

}
