package com.lulobank.otp.sdk.dto.ivr;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateAuthorizationIvrResponse {
    private String productNumber;

    public ValidateAuthorizationIvrResponse(String productNumber) {
        this.productNumber = productNumber;
    }
}
