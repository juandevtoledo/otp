package com.lulobank.otp.sdk.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class Withdrawal {
  private BigInteger amount;
  private String documentId;
}

