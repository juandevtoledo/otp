package com.lulobank.otp.services.v3.domain.transactions;

import org.junit.Before;
import org.junit.Test;

import static com.lulobank.otp.services.v3.Sample.buildTransferLulo;
import static org.junit.Assert.assertEquals;

public class TransferLuloTest {
    private TransferLulo transferLulo1;
    private TransferLulo transferLulo2;

    @Before
    public void init() {
        transferLulo1 = buildTransferLulo();
        transferLulo2 = buildTransferLulo();
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(transferLulo1,transferLulo2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(transferLulo1.hashCode(),transferLulo2.hashCode());
    }

}
