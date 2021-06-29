package com.lulobank.otp.services.v3.domain.transactions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreditTest {

    @Test
    public void equalityOnSameValuesInstances() {
        String target = "target-1";
        double amount = 100.0;
        String offer = "credit-offer-2";
        int creditTerm = 24;
        Credit c1 = new Credit(target,amount,offer,creditTerm);
        Credit c2 = new Credit(target,amount,offer,creditTerm);

        assertEquals(c1,c2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        String target = "target-1";
        double amount = 100.0;
        String offer = "credit-offer-2";
        int creditTerm = 24;
        Credit c1 = new Credit(target,amount,offer,creditTerm);
        Credit c2 = new Credit(target,amount,offer,creditTerm);

        assertEquals(c1.hashCode(), c2.hashCode());
    }


}
