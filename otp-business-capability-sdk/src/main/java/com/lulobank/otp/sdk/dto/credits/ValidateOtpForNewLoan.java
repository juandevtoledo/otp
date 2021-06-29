package com.lulobank.otp.sdk.dto.credits;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ValidateOtpForNewLoan {
  private String idCredit;
  private String idOffer;
  private String otp;
}
