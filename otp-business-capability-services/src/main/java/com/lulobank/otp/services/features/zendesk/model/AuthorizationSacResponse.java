package com.lulobank.otp.services.features.zendesk.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizationSacResponse {

    private String otp;
    private String email;
}
