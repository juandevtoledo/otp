package com.lulobank.otp.sdk.dto.zendesk;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ValidateAuthorizationSacRequest  {

    private String transactionType;
    private String clientId;
    private String agentId;
    private String productNumber;
    private String otp;
}
