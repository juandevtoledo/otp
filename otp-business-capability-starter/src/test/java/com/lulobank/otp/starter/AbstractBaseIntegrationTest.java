package com.lulobank.otp.starter;

import co.com.lulobank.tracing.core.BraveTracerWrapper;
import co.com.lulobank.tracing.sqs.SqsBraveTemplate;
import co.com.lulobank.tracing.sqs.SqsListenerAdapter;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.otp.services.features.onboardingotp.otp.OtpService;
import com.lulobank.otp.services.inboundadapters.HireCreditAdapter;
import com.lulobank.otp.services.inboundadapters.VerifyHireCreditOperationAdapter;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpIvrRepository;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.outbounadadapters.services.IMessageToSend;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import com.lulobank.otp.services.outbounadadapters.services.SMSMessageSender;
import com.lulobank.otp.services.ports.out.messaging.dto.SMSNotificationMessage;
import com.lulobank.otp.services.v3.port.in.DeleteOTPCardlessWithdrawalPort;
import com.lulobank.otp.services.v3.port.in.zendesk.SacGenerateAuthorizationPort;
import com.lulobank.otp.services.v3.port.in.zendesk.SacVerifyAuthorizationPort;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import com.lulobank.otp.services.ports.out.PushNotificationService;
import com.lulobank.otp.starter.utils.AWSCognitoBearerTokenRequestPostProcessor;
import com.lulobank.otp.starter.utils.LuloMockRestServer;
import com.lulobank.otp.starter.v3.adapters.out.sqs.NotifyEmailSqsAdapter;
import com.lulobank.otp.starter.v3.adapters.out.sqs.NotifySMSSqsAdapter;
import okhttp3.OkHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@ImportAutoConfiguration(RefreshAutoConfiguration.class)
@ActiveProfiles(profiles = "test")
@WebMvcTest({HireCreditAdapter.class, VerifyHireCreditOperationAdapter.class})
public abstract class AbstractBaseIntegrationTest {
  protected static final String CONTENT_TYPE_JSON = "application/json";
  private static final String tenantAWSTokenZendesk =
          "eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjUyMjMxIiwic3ViIjoic3ViamVjdCIsInNjb3BlIjoiemVuZGVzay96ZW5kZXNrIiwiZXhwIjo0NjgzODA1MTQxfQ.ADohbBJ41eLr9Znp-RCiwpmK-woaaeXsNQMFatr7nUI-_kC0arlkav343rYKcjsTHyiDaa51Vh9aMr1XyKJSaURBaGuUIvh0Ic-4kUy2toHXPrrI9oezd8KsGdLcA53OYjYX2HzEeVwEoqQ-8BqMuEe6lL3M1-OSZ_2STSMBsv6m2Uf9FOhk9lr91AYDINfj-ELUlqBAsoLMyJTjl2HmGgHYZvyVELe-hmbeUjffJcqtN2yhyUf2FsNodpD2rgm39syG5e4PhXtwoh9K0kbN3hBP5qGijSgkX6AriFxO4ikZdHx4RztYJL55uzHH5IXKV2XT9v6ASdUG17rfjjt0Pw";


  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;
  @Autowired
  protected LuloMockRestServer mockWebServer;


  @MockBean
  private KeyValRepository keyValRepository;
  @MockBean
  protected OtpRepository otpRepository;
  @MockBean
  protected OtpIvrRepository otpIvrRepository;
  @MockBean
  protected SMSMessageSender smsMessageSender;
  @MockBean
  protected DeleteOTPCardlessWithdrawalPort deleteOTPCardlessWithdrawalPort;
  @MockBean
  protected NotifySMSSqsAdapter notifySMSSqsAdapter;
  @MockBean
  protected NotifyEmailSqsAdapter notifyEmailSqsAdapter;
  @MockBean
  protected SqsListenerAdapter sqsListenerAdapter;
  @MockBean(name="okHttpBuilderTracing")
  protected OkHttpClient builder;
  @MockBean
  protected BraveTracerWrapper braveTracerWrapper;
  @MockBean
  protected SqsBraveTemplate sqsBraveTemplate;

  @MockBean
  protected OtpService otpService;
  @MockBean
  protected SimpleMessageListenerContainer simpleMessageListenerContainer;
  @MockBean
  protected MailSender mailSender;
  @MockBean
  @Qualifier("retrofitClientOperations")
  protected RetrofitClientOperations retrofitClientOperations;
  @MockBean
  protected RedisConnectionFactory redisConnectionFactory;
  @MockBean
  protected RedisTemplate<?, ?> redisTemplate;
  @MockBean
  protected PushNotificationService pushNotificationService;
  @MockBean
  protected SacGenerateAuthorizationPort sacGenerateAuthorizationPort;
  @MockBean
  protected SacVerifyAuthorizationPort sacVerifyAuthorizationPort;
  @Captor
  protected ArgumentCaptor<IMessageToSend> smsMessageCaptor;
  @Captor
  protected ArgumentCaptor<SMSNotificationMessage> smsNotificationMessageCaptor;
  @Captor
  protected ArgumentCaptor<String> stringArgumentCaptor;
  @Captor
  protected ArgumentCaptor<CreatePlatformEndpointRequest> createPlatformEndpointRequestArgumentCaptor;
  @Captor
  protected ArgumentCaptor<SetEndpointAttributesRequest> setEndpointAttributesRequestArgumentCaptor;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    init();
  }

  @After
  public void clean() {
    this.mockWebServer.reset();
  }


  protected abstract void init();


  protected static AWSCognitoBearerTokenRequestPostProcessor bearerTokenAWSZendesk() {
    return new AWSCognitoBearerTokenRequestPostProcessor(tenantAWSTokenZendesk);
  }

}
