package com.lulobank.otp.services.utils;

import com.lulobank.otp.sdk.dto.AbstractOperation;
import com.lulobank.otp.sdk.dto.external.AbstractExternalOperation;
import com.lulobank.otp.sdk.dto.ivr.AuthorizationIvrRequest;
import com.lulobank.otp.services.outbounadadapters.model.OtpIvrRedisEntity;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import io.vavr.control.Option;
import org.apache.logging.log4j.util.Strings;

public class OtpRedisEntityUtils {
  public static final String REDIS_OTP_PREFIX = "otp-business-capability";
  public static final String REDIS_OTP_KEY_CONNECTOR = ":";
  public static final int OPERATION_EXPIRATION_TIME = 600;
  private static final int OTP_EMAIL_EXPIRATION_TIME = 180;
  private static final int OTP_IVR_EXPIRATION_TIME = 300;
  private static final int OTP_SMS_EXPIRATION_TIME = 300;

  public static OtpRedisEntity getOtpRedisEntityFromEmailTemplate(EmailTemplate emailTemplate, String otp) {
    return new OtpRedisEntity(getOtpRedisEntityKeyFromEmailTemplate(emailTemplate), otp, OTP_EMAIL_EXPIRATION_TIME);
  }

  public static OtpRedisEntity getOtpRedisEntityFromPrefixAndPhoneNumber(String prefix, String phoneNumber, String otp) {
    return new OtpRedisEntity(getOtpRedisEntityKeyFromPrefixAndPhoneNumber(prefix, phoneNumber), otp,
      OTP_SMS_EXPIRATION_TIME);
  }

  public static OtpRedisEntity getOtpRedisEntityFromAbstractExternalOperation(
    AbstractExternalOperation abstractExternalOperation, String otp, int expires) {
    return new OtpRedisEntity(OperationUtils.getOperationHash(abstractExternalOperation), otp, expires);
  }

  public static OtpRedisEntity getOtpRedisEntityFromAbstractOperation(AbstractOperation abstractOperation, String otp) {
    return new OtpRedisEntity(getOtpRedisEntityKeyFromAbstractOperation(abstractOperation), otp, OPERATION_EXPIRATION_TIME);
  }

  public static String getOtpRedisEntityKeyFromEmailTemplate(EmailTemplate emailTemplate) {
    return emailTemplate.getTo().get(0);
  }

  public static String getOtpRedisEntityKeyFromPrefixAndPhoneNumber(String prefix, String phoneNumber) {
    StringBuilder key = new StringBuilder();
    key.append(prefix).append('|').append(phoneNumber);
    return key.toString();
  }

  public static String getOtpRedisEntityKeyFromAbstractOperation(AbstractOperation abstractOperation) {
    StringBuilder sb = new StringBuilder(REDIS_OTP_PREFIX);
    sb.append('-').append(abstractOperation.getIdClient()).append('-')
      .append(OperationUtils.getOperationHash(abstractOperation));
    return sb.toString();
  }



  public static OtpIvrRedisEntity getOtpRedisEntityFromAuthorizationIvrRequest(AuthorizationIvrRequest authorizationIvrRequest, String otpNumber){
    return new OtpIvrRedisEntity(getOtpRedisEntityKeyFromAuthorizationIvrRequest(authorizationIvrRequest),
            otpNumber, OTP_IVR_EXPIRATION_TIME, authorizationIvrRequest.getProductNumber());
  }

  public static String getOtpRedisEntityKeyFromAuthorizationIvrRequest (AuthorizationIvrRequest authorizationIvrRequest){
    StringBuilder key = new StringBuilder();
    key.append(authorizationIvrRequest.getDocumentNumber()).append(authorizationIvrRequest.getTransactionType())
            .append(getNumberProduct(authorizationIvrRequest.getProductNumber()));
    return key.toString();
  }

  private static String getNumberProduct(String numberProduct){
    return Option.of(numberProduct)
            .filter(number->number.length()>4)
            .map(number->number.substring(number.length()-4))
            .getOrElse(()-> Strings.EMPTY);
  }

  public static String generateOtpIdForNewLoan(String idClient, String idCredit, String idOffer) {
    return new StringBuilder("")
              .append(REDIS_OTP_PREFIX)
              .append(REDIS_OTP_KEY_CONNECTOR)
              .append(idClient).append(REDIS_OTP_KEY_CONNECTOR)
              .append(idCredit).append(REDIS_OTP_KEY_CONNECTOR)
              .append(idOffer).toString();
  }

  public static String verifyOtpIdForNewLoan(String idClient, String idCredit, String idOffer) {
    return new StringBuilder("")
            .append(REDIS_OTP_PREFIX)
            .append(REDIS_OTP_KEY_CONNECTOR)
            .append(idClient).append(REDIS_OTP_KEY_CONNECTOR)
            .append(idCredit).append(REDIS_OTP_KEY_CONNECTOR)
            .append(idOffer).toString();
  }
}
