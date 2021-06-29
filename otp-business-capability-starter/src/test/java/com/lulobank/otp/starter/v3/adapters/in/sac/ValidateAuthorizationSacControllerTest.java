package com.lulobank.otp.starter.v3.adapters.in.sac;

import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import com.lulobank.otp.starter.AbstractBaseIntegrationTest;
import com.lulobank.otp.starter.v3.adapters.Sample;
import com.lulobank.otp.starter.v3.adapters.in.sac.dto.VerifyAuthorizationSacRequest;
import io.vavr.control.Either;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ValidateAuthorizationSacControllerTest extends AbstractBaseIntegrationTest {

    public static final String VERIFY_SAC_ATHORIZATION = "/verify/authorization-sac";

    @Value("classpath:mocks/sac/bad-request.json")
    private Resource badRequest;

    @Value("classpath:mocks/sac/error-repository.json")
    private Resource errorRepository;


    @Override
    protected void init() {

    }

    @Test
    public void shouldReturnOk() throws Exception {

        VerifyAuthorizationSacRequest verifyAuthorizationSacRequest = Sample.getVerifyAuthorizationSacRequest();
        when(sacVerifyAuthorizationPort.execute(any())).thenReturn(Either.right(true));

        mockMvc.perform(MockMvcRequestBuilders
                .post(VERIFY_SAC_ATHORIZATION)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(verifyAuthorizationSacRequest))
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequest() throws Exception {

        VerifyAuthorizationSacRequest verifyAuthorizationSacRequest = Sample.getVerifyAuthorizationSacRequest();
        verifyAuthorizationSacRequest.setTransactionType(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post(VERIFY_SAC_ATHORIZATION)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(verifyAuthorizationSacRequest))
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(FileUtils.readFileToString(badRequest.getFile(), StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldReturnBadGateway() throws Exception {

        VerifyAuthorizationSacRequest verifyAuthorizationSacRequest = Sample.getVerifyAuthorizationSacRequest();
        when(sacVerifyAuthorizationPort.execute(any())).thenReturn(Either.left(KeyValRepositoryError.connectionError()));

        mockMvc.perform(MockMvcRequestBuilders
                .post(VERIFY_SAC_ATHORIZATION)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(verifyAuthorizationSacRequest))
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadGateway())
                .andExpect(content().json(FileUtils.readFileToString(errorRepository.getFile(), StandardCharsets.UTF_8)));
    }
}
