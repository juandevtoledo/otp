package com.lulobank.otp.services.v3.domain.transactions;

import org.junit.Before;
import org.junit.Test;

import static com.lulobank.otp.services.v3.Sample.buildTransferInterbank;
import static org.junit.Assert.assertEquals;

public class TransferInterbankTest {
    private TransferInterbank transferInterbank1;
    private TransferInterbank transferInterbank2;

    @Before
    public void init() {
        transferInterbank1 = buildTransferInterbank();
        transferInterbank2 = buildTransferInterbank();
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(transferInterbank1, transferInterbank2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(transferInterbank1.hashCode(), transferInterbank2.hashCode());
    }
}