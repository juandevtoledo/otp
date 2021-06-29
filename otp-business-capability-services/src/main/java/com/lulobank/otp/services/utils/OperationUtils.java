package com.lulobank.otp.services.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OperationUtils {
  private static final Logger logger = LoggerFactory.getLogger(OperationUtils.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  private OperationUtils() {
  }

  public static String getOperationHash(Object operation) {
    try {
      return Base64.getEncoder()
               .encodeToString(objectMapper.writeValueAsString(operation).getBytes(StandardCharsets.UTF_8));
    } catch (JsonProcessingException e) {
      logger.error("error generating the operation hash", e);
      throw new UnsupportedOperationException(e);
    }
  }
}
