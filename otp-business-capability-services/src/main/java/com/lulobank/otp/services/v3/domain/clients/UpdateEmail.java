package com.lulobank.otp.services.v3.domain.clients;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmail implements OTPTransactionData {
    @NotNull
    @Email
    private String newEmail;
}
