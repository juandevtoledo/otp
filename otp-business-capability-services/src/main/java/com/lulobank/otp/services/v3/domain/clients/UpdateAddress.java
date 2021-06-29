package com.lulobank.otp.services.v3.domain.clients;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddress implements OTPTransactionData {
    @NotNull
    private String address;
    @NotNull
    private String addressPrefix;
    @NotNull
    private String addressComplement;
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
