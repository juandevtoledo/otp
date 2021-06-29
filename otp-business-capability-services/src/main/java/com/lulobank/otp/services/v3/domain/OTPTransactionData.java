package com.lulobank.otp.services.v3.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.services.exceptions.NotValidTransactionDataException;
import io.vavr.control.Try;
import lombok.CustomLog;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static io.vavr.API.$;
import static io.vavr.API.Case;


public interface OTPTransactionData {

    default Try<String> calculateOtpKey(String salt) {
        return Try.of(() -> this.getClass().getCanonicalName().hashCode() +
                "-" + salt.hashCode() +
                "-" + this.hashCode());
    }

    @CustomLog
    class TransactionTypeMapper {

        static final ObjectMapper mapper = new ObjectMapper();

        static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        @SuppressWarnings("unchecked")
        static <T extends OTPTransactionData> Try<T> transactionDataMapper(JsonNode payload, Class<T> type) {
            return Try.of(() -> mapper.treeToValue(payload, type))
                    .filter(data -> isEmpty(data))
                    .onFailure(error -> log.error("Error in payload {} ",error.getMessage(),error))
                    .mapFailure(Case($(), error
                            -> new NotValidTransactionDataException(type, payload.asText(), error)));
        }

        private static <T extends OTPTransactionData> boolean isEmpty(T data) {
            Set<ConstraintViolation<T>> v = validator.validate(data);
            v.stream().forEach(e->log.info(" Error validation " +e.getMessage()));
            return v.isEmpty();
        }
    }
}