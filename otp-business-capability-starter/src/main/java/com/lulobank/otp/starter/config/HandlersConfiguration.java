package com.lulobank.otp.starter.config;

import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.core.Response;
import com.lulobank.core.actions.Action;
import com.lulobank.core.crosscuttingconcerns.PostActionsDecoratorHandler;
import com.lulobank.core.crosscuttingconcerns.ValidatorDecoratorHandler;
import com.lulobank.core.validations.Validator;
import com.lulobank.otp.sdk.dto.onboarding.OtpResponse;
import com.lulobank.otp.services.actions.OtpOperationMessage;
import com.lulobank.otp.services.features.ivr.OtpAuthorizationIvrHandler;
import com.lulobank.otp.services.features.ivr.ValidateAuthorizationIvrHandler;
import com.lulobank.otp.services.features.onboardingotp.CreateOtpEmailHandler;
import com.lulobank.otp.services.features.onboardingotp.CreateOtpHandler;
import com.lulobank.otp.services.features.onboardingotp.VerifyOtpEmailHandler;
import com.lulobank.otp.services.features.onboardingotp.VerifyOtpHandler;
import com.lulobank.otp.services.features.onboardingotp.model.OtpEmailRequest;
import com.lulobank.otp.services.features.onboardingotp.model.OtpRequest;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.features.onboardingotp.validators.*;
import com.lulobank.otp.services.features.otp.InitExternalOperationHandler;
import com.lulobank.otp.services.features.otp.InitOperationHandler;
import com.lulobank.otp.services.features.otp.VerifyExternalOperationHandler;
import com.lulobank.otp.services.features.otp.VerifyOperationHandler;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpIvrRepository;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.starter.v3.adapters.out.sqs.CreateOtpEmailSqsAdapter;
import com.lulobank.otp.starter.v3.adapters.out.sqs.CreateOtpSMSSqsAdapter;
import com.lulobank.otp.starter.v3.adapters.out.sqs.NotifyEmailSqsAdapter;
import com.lulobank.otp.starter.v3.adapters.out.sqs.NotifySMSSqsAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class HandlersConfiguration {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private OtpIvrRepository otpIvrRepository;

    @Autowired
    @Qualifier("retrofitClientOperations")
    private RetrofitClientOperations retrofitClientOperations;

    @Autowired
    private OtpService otpService;

    @Autowired
    private Map<String, OtpOperationMessage> operationMessages;

    @Value("${cloud.aws.sqs.queue.client-alerts-events}")
    private String clientAlertsSqsEndpoint;

    @Bean
    public InitOperationHandler initOperationHandler() {
        return new InitOperationHandler(otpRepository, operationMessages, otpService, retrofitClientOperations);
    }

    @Bean
    public VerifyOperationHandler verifyOperationHandler() {
        return new VerifyOperationHandler(otpRepository);
    }

    @Bean
    public InitExternalOperationHandler initExternalOperationHandler() {
        return new InitExternalOperationHandler(otpRepository, otpService, operationMessages);
    }

    @Bean
    public VerifyExternalOperationHandler verifyExternalOperationHandler() {
        return new VerifyExternalOperationHandler(otpRepository);
    }

    @Bean
    public NotifySMSSqsAdapter getNotifySMSSqsAdapter(SqsBraveTemplate sqsBraveTemplate){
        return new NotifySMSSqsAdapter(clientAlertsSqsEndpoint, sqsBraveTemplate);
    }

    @Bean
    public NotifyEmailSqsAdapter getNotifyEmailSqsAdapter(SqsBraveTemplate sqsBraveTemplate){
        return new NotifyEmailSqsAdapter(clientAlertsSqsEndpoint, sqsBraveTemplate);
    }

    @Bean
    @Qualifier("createOtpEmailHandler")
    public ValidatorDecoratorHandler createOtpEmailHandler(SqsBraveTemplate sqsBraveTemplate) {
        List<Validator<OtpEmailRequest>> validators = new ArrayList<>();
        validators.add(new EmailValidator());
        List<Action<Response<OtpResponse>, OtpEmailRequest>> actions = new ArrayList<>();
        actions.add(new CreateOtpEmailSqsAdapter(clientAlertsSqsEndpoint, sqsBraveTemplate));
        return new ValidatorDecoratorHandler<>(
                new PostActionsDecoratorHandler<>(new CreateOtpEmailHandler(otpRepository), actions), validators);
    }

    @Bean
    @Qualifier("verifyOtpEmailHandler")
    public ValidatorDecoratorHandler verifyOtpEmailHandler() {
        List<Validator<OtpEmailRequest>> validators = new ArrayList<>();
        validators.add(new EmailValidator());
        validators.add(new OtpEmailRequestValidator());
        return new ValidatorDecoratorHandler<>(new VerifyOtpEmailHandler(otpRepository, retrofitClientOperations), validators);
    }

    @Bean
    @Qualifier("createOtpHandler")
    public ValidatorDecoratorHandler createOtpHandler(SqsBraveTemplate sqsBraveTemplate) {
        List<Validator<OtpRequest>> validators = new ArrayList<>();
        validators.add(new OtpRequestValidatePrefix());
        validators.add(new OtpRequestValidatePhoneNumber());
        List<Action<Response<OtpResponse>, OtpRequest>> actions = new ArrayList<>();
        actions.add(new CreateOtpSMSSqsAdapter(clientAlertsSqsEndpoint, sqsBraveTemplate));
        return new ValidatorDecoratorHandler<>(new PostActionsDecoratorHandler<>(new CreateOtpHandler(otpRepository), actions),
                validators);
    }

    @Bean
    @Qualifier("verifyOtpHandler")
    public ValidatorDecoratorHandler verifyOtpHandler() {
        List<Validator<OtpRequest>> validators = new ArrayList<>();
        validators.add(new OtpRequestValidatePrefix());
        validators.add(new OtpRequestValidatePhoneNumber());
        validators.add(new OtpRequestValidateOtpFormat());
        return new ValidatorDecoratorHandler<>(new VerifyOtpHandler(otpRepository), validators);
    }

    @Bean
    public ValidateAuthorizationIvrHandler validateAuthorizationIvrHandler() {
        return new ValidateAuthorizationIvrHandler(otpIvrRepository);
    }

    @Bean
    public OtpAuthorizationIvrHandler otpAuthorizationIvrHandler() {
        return new OtpAuthorizationIvrHandler(otpIvrRepository);
    }
}
