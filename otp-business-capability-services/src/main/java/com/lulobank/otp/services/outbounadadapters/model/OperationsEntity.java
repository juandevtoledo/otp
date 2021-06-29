package com.lulobank.otp.services.outbounadadapters.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "otp")
public class OperationsEntity implements Serializable {
  private static final long serialVersionUID = 1L;
  private String clientId;
  private String operationHash;
  private String otp;
  private String operationId;

  public OperationsEntity(String clientId, String operationHash, String otp) {
    this.clientId = clientId;
    this.operationHash = operationHash;
    this.otp = otp;
  }
}
