package com.lulobank.otp.services.v3.domain.limits;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class UpdateChannelLimitsRequestTest {

    private UpdateChannelLimitsRequest updateChannelLimitsRequest1;
    private UpdateChannelLimitsRequest updateChannelLimitsRequest2;

    @Before
    public void init() {
        String channelName = "TRANSFERENCIAS_P2P";
        BigDecimal actualAmount = BigDecimal.valueOf(10000.0);
        BigDecimal updateAmount = BigDecimal.valueOf(20000.0);

        UpdateChannelLimitsRequest.Channel channel1 = new UpdateChannelLimitsRequest.Channel();
        channel1.setChannel(channelName);
        channel1.setActualAmount(actualAmount);
        channel1.setUpdateAmount(updateAmount);
        updateChannelLimitsRequest1 = new UpdateChannelLimitsRequest();
        updateChannelLimitsRequest1.setChannelsToUpdate(Collections.singletonList(channel1));

        UpdateChannelLimitsRequest.Channel channel2 = new UpdateChannelLimitsRequest.Channel();
        channel2.setChannel(channelName);
        channel2.setActualAmount(actualAmount);
        channel2.setUpdateAmount(updateAmount);
        updateChannelLimitsRequest2 = new UpdateChannelLimitsRequest();
        updateChannelLimitsRequest2.setChannelsToUpdate(Collections.singletonList(channel2));
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(updateChannelLimitsRequest1, updateChannelLimitsRequest2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(updateChannelLimitsRequest1.hashCode(), updateChannelLimitsRequest2.hashCode());
    }
}
