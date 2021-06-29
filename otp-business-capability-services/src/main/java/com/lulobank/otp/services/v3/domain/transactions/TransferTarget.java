package com.lulobank.otp.services.v3.domain.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferTarget {
    @NotBlank
    private String prefix;
    @NotBlank
    private String phone;
    @NotBlank
    private String name;
    private String targetAccount;
    private String targetAccountHolder;
}
