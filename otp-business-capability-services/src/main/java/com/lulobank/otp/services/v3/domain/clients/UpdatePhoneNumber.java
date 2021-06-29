package com.lulobank.otp.services.v3.domain.clients;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public class UpdatePhoneNumber implements OTPTransactionData {
    @NotNull
    private String newPhoneNumber;
    @NotNull
    private Integer countryCode;

}
