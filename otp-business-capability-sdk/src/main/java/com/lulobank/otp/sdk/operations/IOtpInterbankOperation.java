package com.lulobank.otp.sdk.operations;

import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrResponse;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrResponse;
import com.lulobank.otp.sdk.dto.zendesk.ValidateAuthorizationSacRequest;

import java.util.Map;

public interface IOtpInterbankOperation {
  boolean generateOtp(Map<String, String> headers, String prefix, String phonenumber);

  boolean validateOtp(Map<String, String> headers, String prefix, String phonenumber, String token);

  boolean generateEmailOtp(Map<String, String> headers, String email);

  boolean validateEmailOtp(Map<String, String> headers, String email, String token);

  boolean validateAuthorizationSac (Map<String,String> headers, ValidateAuthorizationSacRequest validateAuthorizationSacRequest);

  ValidateAuthorizationIvrResponse validateAuthorizationIvrInternal (Map<String, String> headers, ValidateAuthorizationIvrRequest validateAuthorizationIvrRequest);

  AuthorizationIvrResponse generateOtpIvrInternal(Map<String, String> headers, AuthorizationIvrRequest authorizationIvrRequest);

}
