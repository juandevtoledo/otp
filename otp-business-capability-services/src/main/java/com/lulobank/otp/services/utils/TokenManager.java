package com.lulobank.otp.services.utils;

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
        if (expiresIn != null){
            return expiresIn;
        }
        return  0L;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
