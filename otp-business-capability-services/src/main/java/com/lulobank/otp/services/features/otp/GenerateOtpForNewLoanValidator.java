package com.lulobank.otp.services.features.otp;

import com.lulobank.core.validations.ValidationResult;
import com.lulobank.core.validations.Validator;
import com.lulobank.otp.services.utils.ErrorsOTP;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class GenerateOtpForNewLoanValidator implements Validator<GenerateOtpForNewLoanCommand> {

    @Override
    public ValidationResult validate(GenerateOtpForNewLoanCommand generateOtpForNewLoan) {
        ValidationResult result = null;
        if (isBlank(generateOtpForNewLoan.getIdClient()) ||
                isBlank(generateOtpForNewLoan.getIdCredit()) ||
                isBlank(generateOtpForNewLoan.getIdOffer())) {
            result = new ValidationResult("IdClient, IdCredit, IdOffer, phone or prefixPhone are fields required.", ErrorsOTP.BAD_REQUEST);
        }

        return result;
    }
}


