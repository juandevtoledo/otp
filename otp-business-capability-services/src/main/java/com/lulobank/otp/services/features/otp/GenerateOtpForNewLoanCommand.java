package com.lulobank.otp.services.features.otp;

import com.lulobank.core.Command;
import com.lulobank.otp.sdk.dto.AbstractCommandFeatures;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateOtpForNewLoanCommand extends AbstractCommandFeatures implements Command {

    private String idClient;
    private String idCredit;
    private String idOffer;

}
