package com.lulobank.otp.services.v3.domain.deleteotptoken;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractExternalOperation {
    private String operationId;
}
