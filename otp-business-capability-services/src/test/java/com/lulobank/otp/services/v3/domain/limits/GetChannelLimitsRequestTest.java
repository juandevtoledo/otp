package com.lulobank.otp.services.v3.domain.limits;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GetChannelLimitsRequestTest {
    private GetChannelLimitsRequest getChannelLimitsRequest1;
    private GetChannelLimitsRequest getChannelLimitsRequest2;

    @Before
    public void init() {
        String product = "SAVINGS";
        String accountType = "CATS";

        getChannelLimitsRequest1 = new GetChannelLimitsRequest(product, accountType);
        getChannelLimitsRequest2 = new GetChannelLimitsRequest(product, accountType);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(getChannelLimitsRequest1, getChannelLimitsRequest2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(getChannelLimitsRequest1.hashCode(), getChannelLimitsRequest2.hashCode());
    }
}
