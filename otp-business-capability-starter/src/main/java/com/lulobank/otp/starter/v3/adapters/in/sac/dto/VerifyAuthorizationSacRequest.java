package com.lulobank.otp.starter.v3.adapters.in.sac.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class VerifyAuthorizationSacRequest {
    @NotBlank(message = "transactionType is empty or null")
    private String transactionType;
    @NotBlank(message = "clientId is empty or null")
    private String clientId;
    @NotBlank(message = "agentId is empty or null")
    private String agentId;
    @NotBlank(message = "productNumber is empty or null")
    private String productNumber;
    @NotBlank(message = "otp is empty or null")
    private String otp;
}
