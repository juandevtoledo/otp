package com.lulobank.otp.sdk.dto.credits;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenerateOtpForNewLoan {
  private String idCredit;
  private String idOffer;
}
