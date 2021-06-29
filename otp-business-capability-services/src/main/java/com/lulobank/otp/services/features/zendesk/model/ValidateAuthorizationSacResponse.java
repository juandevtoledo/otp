package com.lulobank.otp.services.features.zendesk.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidateAuthorizationSacResponse {

    private String otp;
    private String email;
}
