package com.lulobank.otp.services.v3.domain.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UseCaseResponseError {
    private String businessCode;
    private String providerCode;
    private String detail = UseCaseErrorStatus.DEFAULT_DETAIL;

    public UseCaseResponseError(String businessCode, String providerCode) {
        this.businessCode = businessCode;
        this.providerCode = providerCode;
    }

}
