package com.lulobank.otp.services.features.otp;

import com.lulobank.core.Command;
import lombok.Getter;

@Getter
public class ValidateOtpForNewLoanCommand implements Command {
    private final String idClient;
    private final String idCredit;
    private final String idOffer;
    private final String otp;

    public ValidateOtpForNewLoanCommand(String idClient, String idCredit, String idOffer, String otp) {
        this.idClient = idClient;
        this.idCredit = idCredit;
        this.idOffer = idOffer;
        this.otp = otp;
    }
}
