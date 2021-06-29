package com.lulobank.otp.sdk.operations.impl;

import com.lulobank.otp.sdk.dto.credits.ValidateOtpForNewLoan;
import com.lulobank.otp.sdk.operations.exceptions.VerifyHireCreditException;
import com.lulobank.otp.sdk.operations.OtpCreditOperations;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;
import java.util.Map;
import static com.lulobank.utils.client.retrofit.RetrofitFactory.buildRetrofit;

public class RetrofitOtpCreditOperation implements OtpCreditOperations {

    private static final String ERROR_MESSAGE = "Error with OTP validation. Please validate logs details.";
    private Retrofit retrofit;
    private RetrofitOtpCreditOperation.RetrofitOtpCreditOperationServices service;

    public RetrofitOtpCreditOperation(String url) {
        this.retrofit = buildRetrofit(url);
        this.service = this.retrofit.create(RetrofitOtpCreditOperation.RetrofitOtpCreditOperationServices.class);
    }

    public RetrofitOtpCreditOperation(Retrofit retrofit) {
        this.retrofit = retrofit;
        this.service = this.retrofit.create(RetrofitOtpCreditOperation.RetrofitOtpCreditOperationServices.class);
    }

    @Override
    public boolean verifyHireCreditOperation(Map<String, String> headers, ValidateOtpForNewLoan requestPayload, String idClient) {
        try {
            Call<Void> call = this.service.verifyHireCreditOperation(headers,requestPayload,idClient );
            Response<Void> response = call.execute();
            return getVerifyOperationResponseEntity(response);
        } catch (Exception e) {
            throw new VerifyHireCreditException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE, e);
        }
    }

    @NotNull
    private boolean getVerifyOperationResponseEntity(Response<Void> response) {
        if (response.code() != HttpStatus.OK.value()) {
            String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
            throw new VerifyHireCreditException(response.code(), errorBody);
        }
        return true;
    }
    interface RetrofitOtpCreditOperationServices {
        @POST("otp/operations/{idClient}/hire-loan/verify")
        Call<Void> verifyHireCreditOperation(@HeaderMap Map<String, String> headers, @Body ValidateOtpForNewLoan requestPayload, @Path("idClient") String idClient);
    }
}
