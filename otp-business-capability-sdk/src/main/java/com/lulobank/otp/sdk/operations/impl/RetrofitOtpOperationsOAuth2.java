package com.lulobank.otp.sdk.operations.impl;

import com.lulobank.utils.client.retrofit.oauth.ITokenManager;

import static com.lulobank.utils.client.retrofit.oauth.OAuth2RetrofitFactory.buildRetrofit;


public class RetrofitOtpOperationsOAuth2 extends RetrofitOtpOperations {

  public RetrofitOtpOperationsOAuth2(String url, String clientId, String clientSecret, String clientUrl, ITokenManager tokenManager) {
    super(url);
    this.retrofit = buildRetrofit(url, clientId, clientSecret, clientUrl, tokenManager);
    this.service = this.retrofit.create(RetrofitOtpOperations.RetrofitOtpServices.class);
  }
}
