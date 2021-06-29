package com.lulobank.otp.services.inboundadapters;

import com.lulobank.clients.sdk.operations.impl.RetrofitClientOperations;
import com.lulobank.otp.sdk.dto.email.EmailTemplate;
import com.lulobank.otp.services.outbounadadapters.model.OtpIvrRedisEntity;
import com.lulobank.otp.services.outbounadadapters.model.OtpRedisEntity;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpIvrRepository;
import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.outbounadadapters.services.MailSender;
import org.junit.Before;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

public abstract class AbstractBaseUnitTest {

    @Mock
    protected OtpRepository otpRepository;
    @Mock
    protected OtpIvrRepository otpIvrRepository;
    @Mock
    protected RetrofitClientOperations retrofitClientOperations;
    @Mock
    protected MailSender mailSender;
    @Mock
    protected BindingResult bindingResult;

    @Captor
    protected ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    protected ArgumentCaptor<EmailTemplate> emailTemplateArgumentCaptor;
    @Captor
    protected ArgumentCaptor<OtpRedisEntity> otpRedisEntityArgumentCaptor;
    @Captor
    protected ArgumentCaptor<OtpIvrRedisEntity> otpIvrRedisEntityArgumentCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        init();
    }
    protected abstract void init();
}
