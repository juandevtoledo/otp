package com.lulobank.otp.services.v3.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Function1;
import io.vavr.Value;
import io.vavr.collection.Stream;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Test;

import java.io.File;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OTPTransactionTest {

    private static final String DATA_PATH = "src/test/resources/data/v3/domain/payloads";

    @Test
    public void toTransactionDataAtLeastOneSample() {
        Stream.of(OTPTransaction.values())
                .forEach(this::assertAtLeastOneSampleTest);
    }

    @Test
    public void otpKeyEqualityForAllTransactions() {
        Stream.of(OTPTransaction.values())
                .forEach( otpTransaction -> assertSameOtpKey(otpTransaction,findTransactionData(otpTransaction)) );
    }

    /**
     * When will this fail? If the TransactionData class definition associated to the OTPTransaction `otpTransaction`
     * generates different values of the otpKey for the same json data; given the default implementation of
     * {@link OTPTransactionData#calculateOtpKey(String)} if based on instances hashCodes, this will assert that the hashCode implementation
     * is based on the definition fields and not the object's default
     * @param otpTransaction TransactionData enum value under test
     * @param jsonNodeOpt represents the json data
     */
    private void assertSameOtpKey(OTPTransaction otpTransaction, Option<JsonNode> jsonNodeOpt) {
        String idClient = "idClient";

        assertTrue("Invalid jsonNodeOpt for "+otpTransaction,jsonNodeOpt.isDefined());

        Function1<String, String> otpKey = defaultOtpKey -> jsonNodeOpt.toTry()
                .flatMap(otpTransaction::toTransactionData)
                .flatMap(transactionData -> transactionData.calculateOtpKey(idClient ))
                .getOrElse(defaultOtpKey);

        String hashCode1 = otpKey.apply("hash-code1");
        String hashCode2 = otpKey.apply("hash-code2");

        assertEquals("Equally created hashCodes doesn't match for "+otpTransaction,hashCode1,hashCode2);
    }

    /**
     * Asserts that there is at least one valid sample data for all the transactions registered in the enum {@link OTPTransaction}
     * @param otpTransaction
     */
    private void assertAtLeastOneSampleTest(OTPTransaction otpTransaction) {

        Option hasSample = readTransactionSamples(DATA_PATH)
                .map(jsonTry -> jsonTry.flatMap(otpTransaction::toTransactionData))
                .find(Try::isSuccess);

        assertTrue("There is not sample data test for "+otpTransaction, hasSample.isDefined());
    }

    private Option<JsonNode> findTransactionData(OTPTransaction otpTransaction) {
        return readTransactionSamples(DATA_PATH)
                .find( jsonNodeTry -> jsonNodeTry
                        .flatMap(otpTransaction::toTransactionData)
                        .isSuccess()
                ).flatMap(Value::toOption);
    }

    private Stream<Try<JsonNode>> readTransactionSamples(String samplesPath) throws RuntimeException{
        ObjectMapper mapper = new ObjectMapper();

        return Stream.of(requireNonNull(new File(samplesPath).listFiles()))
                .filter(file -> !file.isDirectory())
                .map(file -> Try.of( () -> mapper.readTree(file)));
    }
}
