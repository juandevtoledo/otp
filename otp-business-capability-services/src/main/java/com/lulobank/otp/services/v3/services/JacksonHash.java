package com.lulobank.otp.services.v3.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Base64;

@Slf4j
public class JacksonHash {


    private JacksonHash() {
    }

    public static String apply(JsonNode payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        return Try.of(() -> objectMapper.writeValueAsString(payload))
                .mapTry(JacksonHash::encrypt)
                .onFailure(ex -> log.error("Error in JsonNode -> String", ex))
                .get();
    }


    public static JsonNode of(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        return Try.of(() -> payload)
                .mapTry(JacksonHash::decrypt)
                .mapTry(e -> objectMapper.readValue(e, JsonNode.class))
                .onFailure(ex -> log.error("Error in String -> JsonNode", ex))
                .get();
    }

    public static Option<Tuple2<String, JsonNode>> processHeader(String hash) {
        return Try.of(() -> {
            String[] split = hash.split(":");
            return Tuple.of(split[0], of(split[1]));
        }).onFailure(ex -> log.error("Error processing header", ex))
                .toOption();
    }


    private static String encrypt(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(Charset.defaultCharset()));
    }


    private static String decrypt(String encrypted) {
        return new String(Base64.getDecoder().decode(encrypted), Charset.defaultCharset());
    }

}
