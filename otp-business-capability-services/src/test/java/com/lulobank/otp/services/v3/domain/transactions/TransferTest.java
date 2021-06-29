package com.lulobank.otp.services.v3.domain.transactions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransferTest {

    @Test
    public void equalityOnSameValuesInstances() {
        String target = "target-1";
        double amount = 100.0;
        Transfer t1 = new Transfer(target,amount);
        Transfer t2 = new Transfer(target,amount);

        assertEquals(t1,t2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        String target = "target-1";
        double amount = 100.0;
        Transfer t1 = new Transfer(target,amount);
        Transfer t2 = new Transfer(target,amount);

        assertEquals(t1.hashCode(),t2.hashCode());
    }
}
