package com.lulobank.otp.sdk.operations;

import com.lulobank.utils.client.retrofit.oauth.ITokenManager;

public class TokenManager implements ITokenManager {

    private String token;
    private Long expiresIn;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getExpiresIn() {
        return (expiresIn == null ? 0L : expiresIn);
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
