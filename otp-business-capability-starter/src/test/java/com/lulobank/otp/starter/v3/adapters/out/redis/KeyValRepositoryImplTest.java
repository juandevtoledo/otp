package com.lulobank.otp.starter.v3.adapters.out.redis;

import com.lulobank.otp.services.outbounadadapters.redisrepository.OtpRepository;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryError;
import com.lulobank.otp.services.v3.port.out.redis.error.KeyValRepositoryErrorStatus;
import com.lulobank.otp.starter.v3.adapters.Sample;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static com.lulobank.otp.services.v3.domain.OTPTransaction.TRANSFER;
import static com.lulobank.otp.starter.v3.adapters.Constant.HASH_KEY;
import static com.lulobank.otp.starter.v3.adapters.Constant.OTP;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class KeyValRepositoryImplTest {

    private KeyValRepositoryImpl keyValRepository;
    @Mock
    StringRedisTemplate stringRedisTemplate;

    @Mock
    RedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private HashOperations hashOperations;

    @Captor
    ArgumentCaptor<String> argCaptor;

    @Captor
    ArgumentCaptor<Long> longCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        Mockito.when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        Mockito.doNothing().when(valueOperations).set(anyString(), anyString());
        keyValRepository = new KeyValRepositoryImpl(stringRedisTemplate);
    }

    @Test
    public void save() {
        Try<String> save = keyValRepository.save("678234234", "0000", TRANSFER);
        assertThat(save.get(), is(equalTo("0000")));

        verify(valueOperations).set(argCaptor.capture(), Mockito.anyString(), longCaptor.capture(), Mockito.any());
        assertThat(longCaptor.getValue(), is(equalTo((long) TRANSFER.getArrangement().getExpirationTime())));
    }

    @Test
    public void saveKeyVal(){
        Either<KeyValRepositoryError, String> save = keyValRepository.save("key","0000");

        verify(valueOperations).set(argCaptor.capture(), argCaptor.capture(), anyLong(), any());

        assertTrue(save.isRight());
        assertEquals("0000",save.get());
        assertEquals("key",argCaptor.getAllValues().get(0));
        assertEquals("0000",argCaptor.getAllValues().get(1));

    }

    @Test
    public void saveKeyValError(){

        doThrow(RedisConnectionFailureException.class).when(valueOperations).set(any(),any(),anyLong(),any());

        Either<KeyValRepositoryError, String> save = keyValRepository.save("key","0000");

        verify(valueOperations).set(argCaptor.capture(), argCaptor.capture(), anyLong(), any());

        assertTrue(save.isLeft());
        assertEquals(KeyValRepositoryErrorStatus.OTP_115.name(),save.getLeft().getBusinessCode());
    }

    @Test
    public void getKeyVal(){

        when(valueOperations.get(argCaptor.capture())).thenReturn("0000");

        Either<KeyValRepositoryError, Option<String>> save = keyValRepository.getByKey("key");

        assertTrue(save.isRight());
        assertEquals("0000",save.get().get());
        assertEquals("key",argCaptor.getValue());

    }

    @Test
    public void getKeyValError(){

        when(valueOperations.get(any())).thenThrow(RedisConnectionFailureException.class);

        Either<KeyValRepositoryError, Option<String>> save = keyValRepository.getByKey("key");

        assertTrue(save.isLeft());
        assertEquals(KeyValRepositoryErrorStatus.OTP_115.name(),save.getLeft().getBusinessCode());
    }

}