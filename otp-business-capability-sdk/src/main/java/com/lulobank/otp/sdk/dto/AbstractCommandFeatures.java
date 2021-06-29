package com.lulobank.otp.sdk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@JsonIgnoreProperties(value = { "authorizationHeader" , "httpHeaders"})
public abstract class AbstractCommandFeatures {
    private static final String AUTHORIZATION_TOKEN_KEY = "authorization";
    private Map<String, String> httpHeaders;

    public AbstractCommandFeatures() {
    }

    public AbstractCommandFeatures(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public Map<String, String> getAuthorizationHeader() {
        return Optional.ofNullable(httpHeaders).map(h -> {
            h.keySet().removeIf(key -> !AUTHORIZATION_TOKEN_KEY.equalsIgnoreCase(key));
            return h;
        }).orElse(new HashMap<>());
    }
}
