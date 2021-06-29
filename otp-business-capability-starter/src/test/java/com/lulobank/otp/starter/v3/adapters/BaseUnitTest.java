package com.lulobank.otp.starter.v3.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.otp.services.v3.port.in.DeleteOTPCardlessWithdrawalPort;
import com.lulobank.otp.starter.v3.adapters.out.redis.repository.HashRedisRepository;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseUnitTest {

    @Mock
    protected HashRedisRepository hashRedisRepository;

    @Mock
    protected DeleteOTPCardlessWithdrawalPort deleteOTPCardlessWithdrawalPort;

    @Mock
    protected BindingResult bindingResult;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8082);

    protected ObjectMapper objectMapper = new ObjectMapper();

}
