package com.lulobank.otp.services.v3.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.services.exceptions.NotValidTransactionDataException;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import java.io.IOException;

import static com.lulobank.otp.services.v3.domain.OTPTransactionData.TransactionTypeMapper.transactionDataMapper;
import static org.junit.Assert.*;

public class OTPTransactionDataTest {

    @Test
    public void transactionMapperFullBinding() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree("{\"mandatory1\":\"mandatoryString\",\"mandatory2\":100000.0,\"optional1\":\"optionalString\"}");

        Try<SampleTransaction> sampleTransaction = transactionDataMapper(jsonNode, SampleTransaction.class);

        assertTrue(sampleTransaction.isSuccess());
        assertEquals(sampleTransaction.get(), new SampleTransaction("mandatoryString", 100000.0, "optionalString"));
    }

    @Test
    public void transactionMapperFailsOnMandatoryFieldsMissing() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode1 = mapper.readTree("{\"mandatory1\":\"ddddd\",\"optional1\":\"ddddd\"}");
        JsonNode jsonNode2 = mapper.readTree("{\"mandatory2\":30000.0,\"optional1\":\"ddddd\"}");

        Try<SampleTransaction> sampleTransaction1 = transactionDataMapper(jsonNode1, SampleTransaction.class);
        Try<SampleTransaction> sampleTransaction2 = transactionDataMapper(jsonNode2, SampleTransaction.class);

        sampleTransaction1
                .onFailure( e -> assertTrue(e instanceof NotValidTransactionDataException))
                .onSuccess( t -> fail("The json data binding should fail as mandatory field missing"));
        sampleTransaction2
                .onFailure( e -> assertTrue(e instanceof NotValidTransactionDataException))
                .onSuccess( t -> fail("The json data binding should fail as mandatory field missing"));
    }

    @Test
    public void transactionMapperPartialBinding() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree("{\"mandatory1\":\"mandatoryString\",\"mandatory2\":100000.0 }");

        Try<SampleTransaction> sampleTransaction = transactionDataMapper(jsonNode, SampleTransaction.class);

        assertTrue(sampleTransaction.isSuccess());
        assertEquals(sampleTransaction.get(), new SampleTransaction("mandatoryString", 100000.0, null));
    }

    @Test
    public void calculateOtbKeyEquallyCreatedInstancesSameKey() {
        SampleTransaction s1 = new SampleTransaction("mandatoryString", 100000.0, "optionalValue");
        SampleTransaction s2 = new SampleTransaction("mandatoryString", 100000.0, "optionalValue");

        assertEquals(s1.calculateOtpKey("salt"), s2.calculateOtpKey("salt"));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SampleTransaction implements OTPTransactionData {
        @NotNull
        private String mandatory1;
        @NotNull
        private Double mandatory2;

        private String optional1;
    }
}
