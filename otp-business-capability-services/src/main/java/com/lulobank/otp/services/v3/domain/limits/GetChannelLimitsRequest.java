package com.lulobank.otp.services.v3.domain.limits;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GetChannelLimitsRequest implements OTPTransactionData {
    @NotBlank
    private String product;
    @NotBlank
    private String accountType;
}