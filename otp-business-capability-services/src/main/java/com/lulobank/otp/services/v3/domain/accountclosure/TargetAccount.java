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
public class TargetAccount implements OTPTransactionData {
    @NotNull
    private String prefix;
    @NotNull
    private String phone;
    @NotNull
    private String name;
    @NotNull
    private String account;
    @NotNull
    private String accountHolder;
}
