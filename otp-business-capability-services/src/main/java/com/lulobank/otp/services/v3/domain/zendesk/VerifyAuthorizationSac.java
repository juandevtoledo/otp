package com.lulobank.otp.services.v3.domain.zendesk;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyAuthorizationSac {
    private String transactionType;
    private String clientId;
    private String agentId;
    private String productNumber;
    private String otp;
}
