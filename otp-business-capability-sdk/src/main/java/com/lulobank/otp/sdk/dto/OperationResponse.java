package com.lulobank.otp.sdk.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationResponse {
  private String message;
  private Object data;
  private String challenge;
  private String phone;
  private String prefix;
}
