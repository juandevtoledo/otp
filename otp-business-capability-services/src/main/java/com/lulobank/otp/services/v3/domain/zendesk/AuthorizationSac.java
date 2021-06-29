package com.lulobank.otp.services.v3.domain.zendesk;

import com.lulobank.otp.sdk.dto.AbstractCommandFeatures;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizationSac  extends AbstractCommandFeatures {
    private String transactionType;
    private String clientId;
    private String agentId;
    private String productNumber;
}
