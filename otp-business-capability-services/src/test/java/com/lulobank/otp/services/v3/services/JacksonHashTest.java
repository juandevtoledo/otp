package com.lulobank.otp.services.v3.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.Tuple2;
import io.vavr.control.Option;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class JacksonHashTest {

    private String base64 = "eyJ0YXJnZXQiOiJkZGRkZCIsImFtb3VudCI6MzAwMDAuMH0=";

    @Test
    public void apply() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"ddddd\",\"amount\":30000.0}");
        String apply = JacksonHash.apply(transferJson);
        assertThat(base64, is(CoreMatchers.equalTo(apply)));
    }

    @Test
    public void ofWhenRequestIsOk() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"ddddd\",\"amount\":30000.0}");
        JsonNode apply = JacksonHash.of(base64);
        assertThat(apply, is(CoreMatchers.equalTo(transferJson)));
    }

    @Test
    public void processHeader() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"ddddd\",\"amount\":30000.0}");
        Option<Tuple2<String, JsonNode>> fromToken = JacksonHash.processHeader("2456:" + base64);
        assertThat(fromToken.get()._1(), is(CoreMatchers.equalTo("2456")));
        assertThat(fromToken.get()._2(), is(CoreMatchers.equalTo(transferJson)));
    }

    @Test
    public void processHeaderWhenBadHeader() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode transferJson = mapper.readTree("{\"target\":\"ddddd\",\"amount\":30000.0}");
        Option<Tuple2<String, JsonNode>> fromToken = JacksonHash.processHeader("d/zEotonCzdsudgGMdvprKdnU6VjStXgx");
        assertThat(fromToken.isEmpty(), is(true));
    }
}