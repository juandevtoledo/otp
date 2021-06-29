package com.lulobank.otp.services.v3.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.lulobank.otp.sdk.dto.AbstractCommandFeatures;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OTPValidationRq extends AbstractCommandFeatures {

    private JsonNode payload;

    private String clientId;

    private OTPTransaction transactionType;

    private String otp;

    public OTPValidationRq(JsonNode payload, String clientId, OTPTransaction transactionType) {
        this.payload = payload;
        this.clientId = clientId;
        this.transactionType = transactionType;
    }
}
