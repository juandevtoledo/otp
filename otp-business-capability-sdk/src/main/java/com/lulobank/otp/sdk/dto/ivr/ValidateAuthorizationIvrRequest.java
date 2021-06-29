package com.lulobank.otp.sdk.dto.ivr;

import com.lulobank.core.Command;
import com.lulobank.otp.sdk.dto.AbstractCommandFeatures;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ValidateAuthorizationIvrRequest extends AbstractCommandFeatures implements Command {
    @NotEmpty(message = "transactionType is null or empty")
    private String transactionType;
    @NotEmpty (message = "productNumber is null or empty")
    private String productNumber;
    @NotEmpty (message = "documentNumber is null or empty")
    private String documentNumber;
    @NotEmpty (message = "otp is null or empty")
    private String otp;
}
