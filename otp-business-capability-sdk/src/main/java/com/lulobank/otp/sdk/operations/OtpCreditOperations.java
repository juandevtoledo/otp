package com.lulobank.otp.sdk.operations;

import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;

import java.util.Map;

public interface OtpCreditOperations {
    boolean verifyHireCreditOperation(Map<String, String> headers, ValidateOtpForNewLoan requestPayload, String idClients);
}
