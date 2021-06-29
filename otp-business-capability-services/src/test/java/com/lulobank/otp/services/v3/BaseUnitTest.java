package com.lulobank.otp.services.v3;

import com.lulobank.otp.services.v3.port.out.clients.ClientsPort;
import com.lulobank.otp.services.v3.port.out.notifactions.NotifyService;
import com.lulobank.otp.services.v3.port.out.redis.HashRepositoryPort;
import com.lulobank.otp.services.v3.port.out.redis.KeyValRepository;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseUnitTest {
    @Mock
    protected ClientsPort clientsPort;
    @Mock
    protected KeyValRepository keyValRepository;
    @Mock
    protected NotifyService notifyService;
    @Mock
    protected HashRepositoryPort hashRepositoryPort;

    @Captor
    protected ArgumentCaptor<Map<String, String>> headersCaptor;
    @Captor
    protected ArgumentCaptor<String> idCardCaptor;
}
