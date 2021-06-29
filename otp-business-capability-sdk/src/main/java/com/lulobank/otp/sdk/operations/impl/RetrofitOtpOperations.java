package com.lulobank.otp.sdk.operations.impl;

import com.lulobank.otp.sdk.dto.exceptions.AuthorizationIvrException;
import com.lulobank.otp.sdk.dto.exceptions.AuthorizationSacException;
import com.lulobank.otp.sdk.dto.exceptions.OtpValidationException;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrResponse;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrRequest;
import com.lulobank.otp.sdk.dto.ivr.ValidateAuthorizationIvrResponse;
import com.lulobank.otp.sdk.dto.zendesk.ValidateAuthorizationSacRequest;
import com.lulobank.otp.sdk.operations.IOtpInterbankOperation;
import com.lulobank.utils.exception.ServiceException;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.io.IOException;
import java.util.Map;

import static com.lulobank.utils.client.retrofit.RetrofitFactory.buildRetrofit;


public class RetrofitOtpOperations implements IOtpInterbankOperation {
  private static final Logger LOGGER = LoggerFactory.getLogger(RetrofitOtpOperations.class);
  private static final String ERROR_MESSAGE_VALIDATE_AUTHORIZATION_SAC = "Error OTP service ValidateAuthorizationSac";
  private static final String ERROR_MESSAGE_GENERATE_OTP = "Error OTP service GenerateOtp";
  private static final String ERROR_MESSAGE_GENERATE_EMAIL_OTP = "Error OTP service GenerateEmailOtp";
  private static final String ERROR_MESSAGE_VALIDATE_OTP = "Error OTP service ValidateOtp";
  private static final String ERROR_MESSAGE_VALIDATE_EMAIL_OTP = "Error OTP service ValidateEmailOtp";
  private static final String EMAIL = "email";
  protected Retrofit retrofit;
  protected RetrofitOtpServices service;

  public RetrofitOtpOperations(String url) {
    this.retrofit = buildRetrofit(url);
    this.service = this.retrofit.create(RetrofitOtpServices.class);
  }

  @Override
  public boolean generateOtp(Map<String, String> headers, String prefix, String phonenumber) {
    Call<Void> call = this.service.generateOtp(headers, prefix, phonenumber);
    try {
      Response<Void> response = call.execute();
      return this.getVerifyOperationResponseEntity(response);
    } catch (ServiceException | IOException e) {
      LOGGER.error(ERROR_MESSAGE_GENERATE_OTP, phonenumber);
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE_GENERATE_OTP, e);
    }
  }

  @Override
  public boolean generateEmailOtp(Map<String, String> headers, String email) {
    Call<Void> call = this.service.generateEmailOtp(headers, email);
    try {
      Response<Void> response = call.execute();
      return this.getVerifyOperationResponseEntity(response);
    } catch (ServiceException | IOException e) {
      LOGGER.error(ERROR_MESSAGE_GENERATE_EMAIL_OTP, email);
      throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE_GENERATE_EMAIL_OTP, e);
    }
  }

  @Override
  public boolean validateOtp(Map<String, String> headers, String prefix, String phonenumber, String token) {
    Call<Void> call = this.service.validateOtp(headers, prefix, phonenumber, token);
    try {
      Response<Void> response = call.execute();
      return this.getVerifyOperationResponseEntity(response);
    } catch (ServiceException | IOException e) {
      LOGGER.error(ERROR_MESSAGE_VALIDATE_OTP, phonenumber, token);
      throw new OtpValidationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE_VALIDATE_OTP, e);
    }
  }

  @Override
  public boolean validateEmailOtp(Map<String, String> headers, String email, String token) {
    Call<Void> call = this.service.validateEmailOtp(headers, email, token);
    try {
      Response<Void> response = call.execute();
      return this.getVerifyEmailOperationResponseEntity(response);
    } catch (ServiceException | IOException e) {
      LOGGER.error(ERROR_MESSAGE_VALIDATE_EMAIL_OTP, email, token);
      throw new OtpValidationException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE_VALIDATE_EMAIL_OTP, e);
    }
  }

  @Override
  public boolean validateAuthorizationSac(Map<String,String> headers, ValidateAuthorizationSacRequest validateAuthorizationSacRequest) {
    Call<Void> call = this.service.validateAuthorizationSacOtp(headers, validateAuthorizationSacRequest);
    try {
      Response<Void> response = call.execute();
      return this.getValidateAuthorizationSacOtp(response);
    } catch (ServiceException | IOException e) {
      LOGGER.error(ERROR_MESSAGE_VALIDATE_AUTHORIZATION_SAC, validateAuthorizationSacRequest.toString());
      throw new AuthorizationSacException(HttpStatus.INTERNAL_SERVER_ERROR.value(), ERROR_MESSAGE_VALIDATE_AUTHORIZATION_SAC, e);
    }
  }

  @Override
  public ValidateAuthorizationIvrResponse validateAuthorizationIvrInternal(Map<String, String> headers, ValidateAuthorizationIvrRequest validateAuthorizationIvrRequest) {
    Call<com.lulobank.core.Response<ValidateAuthorizationIvrResponse>> call = this.service.validateAuthorizationIvrOtpInternal(headers, validateAuthorizationIvrRequest);
    return Try.of(()-> getValidationAuthorizationIvrOtp(call)).onFailure(throwable -> handleValidationAuthorizationIvr(throwable, validateAuthorizationIvrRequest.getDocumentNumber())).get();
  }

  @Override
  public AuthorizationIvrResponse generateOtpIvrInternal(Map<String, String> headers, AuthorizationIvrRequest authorizationIvrRequest) {
    Call<com.lulobank.core.Response<AuthorizationIvrResponse>> call = service.generateOtpByIvrInternal(headers, authorizationIvrRequest);
    return Try.of(() -> getAuthorizationIvrOtp(call)).onFailure(throwable -> handleAuthorizationIvr(throwable, authorizationIvrRequest.getDocumentNumber())).get();
  }

  private ValidateAuthorizationIvrResponse getValidationAuthorizationIvrOtp(
          Call<com.lulobank.core.Response<ValidateAuthorizationIvrResponse>> p) throws IOException {
    retrofit2.Response<com.lulobank.core.Response<ValidateAuthorizationIvrResponse>> response = p.execute();
    if (response.body() != null) {
      LOGGER.debug("validateAuthorizationIvrOtpInternal POST: {}", response.body());
    }
    return getValidation(response);
  }

  private AuthorizationIvrResponse getAuthorizationIvrOtp(
          Call<com.lulobank.core.Response<AuthorizationIvrResponse>> call) throws IOException {
    retrofit2.Response<com.lulobank.core.Response<AuthorizationIvrResponse>> response = call.execute();
    if (response.body() != null) {
      LOGGER.debug("generateOtpByIvrInternal POST: {}", response.body());
    }
    return getAuthorizationIvrOtpResponseEntity(response);
  }

  private void handleValidationAuthorizationIvr(Throwable e, String value) {
    LOGGER.error("Error Otp Service validateAuthorizationIvrOtpInternal documentNumber{}", value, e);
    throw new AuthorizationIvrException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error Otp Service validateAuthorizationIvrOtpInternal");
  }

  private void handleAuthorizationIvr(Throwable e, String value) {
    LOGGER.error("Error Otp Service generateOtpByIvrInternal documentNumber{}", value, e);
    throw new AuthorizationIvrException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error Otp Service generateOtpByIvrInternal");
  }

  @NotNull
  public AuthorizationIvrResponse getAuthorizationIvrOtpResponseEntity(
          retrofit2.Response<com.lulobank.core.Response<AuthorizationIvrResponse>> response) throws IOException {
    if (HttpStatus.OK.value() != response.code()) {
      String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
      throw new AuthorizationIvrException(response.code(), errorBody);
    }
    return Option.of(response.body().getContent())
            .get();
  }

  @NotNull
  public ValidateAuthorizationIvrResponse getValidation(
          retrofit2.Response<com.lulobank.core.Response<ValidateAuthorizationIvrResponse>> response) throws IOException {
    if (HttpStatus.OK.value() != response.code()) {
      String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
      throw new AuthorizationIvrException(response.code(), errorBody);
    }
    return Option.of(response.body().getContent())
            .get();
  }


  @NotNull
  private boolean getVerifyOperationResponseEntity(Response<Void> response) {
    if (response.code() != HttpStatus.OK.value()) {
      String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
      throw new OtpValidationException(response.code(), errorBody);
    }
    return true;
  }

  @NotNull
  private boolean getValidateAuthorizationSacOtp(Response<Void> response) {
    if (HttpStatus.OK.value() == response.code()) {
      return true;
    } else if (HttpStatus.FORBIDDEN.value() == response.code()) {
      return false;
    } else {
      String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
      throw new AuthorizationSacException(response.code(), errorBody);
    }
  }

  @NotNull
  private boolean getVerifyEmailOperationResponseEntity(Response<Void> response) {
    if (response.code() == HttpStatus.OK.value()) {
      return true;
    } else if (response.code() == HttpStatus.NOT_ACCEPTABLE.value()) {
      return false;
    } else {
      String errorBody = response.errorBody() != null ? response.errorBody().toString() : "";
      throw new OtpValidationException(response.code(), errorBody);
    }
  }

  interface RetrofitOtpServices {
    @POST("otp/generate/country/{prefix}/phonenumber/{phonenumber}")
    Call<Void> generateOtp(@HeaderMap Map<String, String> headers, @Path("prefix") String prefix,
                           @Path("phonenumber") String phonenumber);

    @GET("otp/verify/country/{prefix}/phonenumber/{phonenumber}/token/{token}")
    Call<Void> validateOtp(@HeaderMap Map<String, String> headers, @Path("prefix") String prefix,
                           @Path("phonenumber") String phonenumber, @Path("token") String token);

    @POST("otp/generate/email/{email}")
    Call<Void> generateEmailOtp(@HeaderMap Map<String, String> headers, @Path(EMAIL) String email);

    @GET("otp/verify/email/{email}/token/{token}")
    Call<Void> validateEmailOtp(@HeaderMap Map<String, String> headers, @Path(EMAIL) String email,
                                @Path("token") String token);

    @POST("otp/verify/authorization-sac")
    Call<Void> validateAuthorizationSacOtp(@HeaderMap Map<String, String> headers,
                                           @Body ValidateAuthorizationSacRequest validateAuthorizationSacRequest);

    @POST("otp/verify/ivrInternal")
    Call<com.lulobank.core.Response<ValidateAuthorizationIvrResponse>> validateAuthorizationIvrOtpInternal(@HeaderMap Map<String, String> headers,
                                                                                                           @Body ValidateAuthorizationIvrRequest validateAuthorizationIvrRequest);
    @POST("otp/ivrInternal")
    Call<com.lulobank.core.Response<AuthorizationIvrResponse>> generateOtpByIvrInternal(

            @HeaderMap Map<String, String> headers, @Body AuthorizationIvrRequest authorizationIvrRequest);


  }
}
