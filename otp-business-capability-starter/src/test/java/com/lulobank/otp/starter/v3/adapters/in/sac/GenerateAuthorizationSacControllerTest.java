package com.lulobank.otp.starter.v3.adapters.in.sac;

import com.lulobank.otp.services.v3.domain.error.GeneralErrorStatus;
import com.lulobank.otp.services.v3.domain.error.UseCaseErrorStatus;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import com.lulobank.otp.starter.AbstractBaseIntegrationTest;
import com.lulobank.otp.starter.v3.adapters.Sample;
import com.lulobank.otp.starter.v3.adapters.in.sac.dto.AuthorizationSacRequest;
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

public class GenerateAuthorizationSacControllerTest extends AbstractBaseIntegrationTest {

    public static final String GENERATE_SAC_ATHORIZATION = "/authorization-sac";

    @Value("classpath:mocks/sac/error-repository.json")
    private Resource errorRepository;

    @Value("classpath:mocks/sac/not-acceptable-generate.json")
    private Resource errorNotAcceptable;

    @Value("classpath:mocks/sac/bad-request.json")
    private Resource badRequest;

    @Override
    protected void init() {

    }

    @Test
    public void shouldReturnOk() throws Exception {

        AuthorizationSacRequest authorizationSacRequest = Sample.getAuthorizationSacRequest();
        when(sacGenerateAuthorizationPort.execute(any())).thenReturn(Either.right(true));

        mockMvc.perform(MockMvcRequestBuilders
                .post(GENERATE_SAC_ATHORIZATION)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(authorizationSacRequest))
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isOk());


    }

    @Test
    public void shouldReturnBadGatewayErrorRepository() throws Exception {

        AuthorizationSacRequest authorizationSacRequest = Sample.getAuthorizationSacRequest();
        when(sacGenerateAuthorizationPort.execute(any())).thenReturn(Either.left(KeyValRepositoryError.connectionError()));

        mockMvc.perform(MockMvcRequestBuilders
                .post(GENERATE_SAC_ATHORIZATION)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(authorizationSacRequest))
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadGateway())
                .andExpect(content().json(FileUtils.readFileToString(errorRepository.getFile(), StandardCharsets.UTF_8)));

    }

    @Test
    public void shouldReturnErrorRepository() throws Exception {

        AuthorizationSacRequest authorizationSacRequest = Sample.getAuthorizationSacRequest();
        authorizationSacRequest.setTransactionType("123");
        when(sacGenerateAuthorizationPort.execute(any())).thenReturn(Either.left(new UseCaseResponseError(UseCaseErrorStatus.OTP_181.name(),
                UseCaseErrorStatus.OTP_181.getMessage(), GeneralErrorStatus.DEFAULT_DETAIL)));

        mockMvc.perform(MockMvcRequestBuilders
                .post(GENERATE_SAC_ATHORIZATION)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(authorizationSacRequest))
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().json(FileUtils.readFileToString(errorNotAcceptable.getFile(), StandardCharsets.UTF_8)));

    }

    @Test
    public void shouldReturnBadRequest() throws Exception {

        AuthorizationSacRequest authorizationSacRequest = Sample.getAuthorizationSacRequest();
        authorizationSacRequest.setTransactionType(null);

        mockMvc.perform(MockMvcRequestBuilders
                .post(GENERATE_SAC_ATHORIZATION)
                .with(bearerTokenAWSZendesk())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(authorizationSacRequest))
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(FileUtils.readFileToString(badRequest.getFile(), StandardCharsets.UTF_8)));

    }


}
