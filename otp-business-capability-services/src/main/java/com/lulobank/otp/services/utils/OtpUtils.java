package com.lulobank.otp.services.utils;

import org.apache.commons.lang3.Validate;

import java.text.NumberFormat;
import java.util.Locale;

public class OtpUtils {
  private OtpUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String getPhonePrefix(String prefix, String phone) {
    return "+" + prefix + phone;
  }

  public static void validatePhoneNumber(String phoneNumer) {
    Validate.notNull(phoneNumer);
    Validate.notEmpty(phoneNumer);
    Validate.isTrue(phoneNumer.matches("((3)[0-9]{9})"));
  }

  public static void validatePrefix(String prefix) {
    Validate.notNull(prefix);
    Validate.notEmpty(prefix);
    Validate.isTrue(prefix.matches("([0-9]{2,3})"));
  }

  public static void validateOtpFormat(String otp) {
    Validate.notNull(otp);
    Validate.notEmpty(otp);
    Validate.isTrue(otp.matches("([0-9]{4})"));
  }

  public static void validateEmail(String email) {
    Validate.notNull(email);
    Validate.notEmpty(email);
    Validate.isTrue(email.matches(
            "(?:[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"));
  }

  public static String getFormattedAmount(Double number) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("es", "CO"));
    numberFormat.setMinimumFractionDigits(2);
    return numberFormat.format(number);
  }
}
