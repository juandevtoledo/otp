package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CloseAccountSimpleCeroBalanceTest {

    private CloseAccountSimpleCeroBalance closeAccountSimpleCeroBalance1;
    private CloseAccountSimpleCeroBalance closeAccountSimpleCeroBalance2;

    @Before
    public void init() {
        String targetAccount = "011722181901";
        String reason = "ACCOUNT CLOSURE";
        DeviceFingerPrint deviceFingerPrint = new DeviceFingerPrint();
        deviceFingerPrint.setHash(new DeviceFingerPrint.Hash("1234"));

        closeAccountSimpleCeroBalance1 = new CloseAccountSimpleCeroBalance(targetAccount, reason, deviceFingerPrint);
        closeAccountSimpleCeroBalance2 = new CloseAccountSimpleCeroBalance(targetAccount, reason, deviceFingerPrint);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(closeAccountSimpleCeroBalance1, closeAccountSimpleCeroBalance2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(closeAccountSimpleCeroBalance1.hashCode(), closeAccountSimpleCeroBalance2.hashCode());
    }

}