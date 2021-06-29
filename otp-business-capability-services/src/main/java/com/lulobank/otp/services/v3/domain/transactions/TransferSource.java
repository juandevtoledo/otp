package com.lulobank.otp.services.v3.domain.transactions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferSource {
    @NotBlank
    private String sourcePrefix;
    @NotBlank
    private String sourcePhone;
    @NotBlank
    private String sourceName;
    @NotBlank
    private String sourceAccount;
    @NotBlank
    private String sourceAccountHolder;
}