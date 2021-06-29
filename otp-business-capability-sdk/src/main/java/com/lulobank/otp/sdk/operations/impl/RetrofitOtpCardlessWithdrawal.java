package com.lulobank.otp.sdk.operations.impl;

import com.lulobank.otp.sdk.dto.external.ExternalOperationResponse;
import com.lulobank.otp.sdk.dto.external.WithdrawalOperation;
import com.lulobank.otp.sdk.operations.IOtpCardlessWithdrawal;
import com.lulobank.utils.exception.ServiceException;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

import java.util.Map;

import static com.lulobank.utils.client.retrofit.RetrofitFactory.buildRetrofit;

public class RetrofitOtpCardlessWithdrawal implements IOtpCardlessWithdrawal {
  private static final String ERROR_MESSAGE = "Error with OTP generation. Please validate logs details.";
  private Retrofit retrofit;
  private RetrofitOtpCardlessWithdrawal.RetrofitOtpCardlessWithdrawalServices service;

  public RetrofitOtpCardlessWithdrawal(String url) {
    this.retrofit = buildRetrofit(url);
    this.service = this.retrofit.create(RetrofitOtpCardlessWithdrawal.RetrofitOtpCardlessWithdrawalServices.class);
  }

  @Override
  public ExternalOperationResponse generateOtpCardlessWithdrawal(Map<String, String> headers, WithdrawalOperation withdrawalOperation) {
    Call<ExternalOperationResponse> call = this.service.generateOtpCardlessWithdrawal(headers, withdrawalOperation);
    try {
      Response<ExternalOperationResponse> response = call.execute();
      return this.getVerifyOperationResponseEntity(response);
    } catch (Exception e) {
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
    }
  }

  @NotNull
  private ExternalOperationResponse getVerifyOperationResponseEntity(Response<ExternalOperationResponse> response) {
    if (response.code() != HttpStatus.OK.value()) {
      String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
      throw new ServiceException(response.code(), errorBody);
    }
    return response.body();
  }

  interface RetrofitOtpCardlessWithdrawalServices {
    @POST("otp/operations/withdrawal")
    Call<ExternalOperationResponse> generateOtpCardlessWithdrawal(@HeaderMap Map<String, String> headers, @Body WithdrawalOperation withdrawalOperation);
  }
}
