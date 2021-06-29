package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CloseAccountLuloTest {

    private CloseAccountLulo closeAccountLulo1;
    private CloseAccountLulo closeAccountLulo2;

    @Before
    public void init() {
        BigDecimal amount = BigDecimal.valueOf(35000.00);
        String currency = "COP";
        String reason = "ACCOUNT CLOSURE";
        String targetAccount = "011722181901";
        String targetAccountHolder = "10323756987";
        String targetPhonePrefix = "57";
        String targetPhone = "30021456897";
        String targetName = "Herschel Shmoikel";
        DeviceFingerPrint deviceFingerPrint = new DeviceFingerPrint();
        deviceFingerPrint.setHash(new DeviceFingerPrint.Hash("1234"));

        closeAccountLulo1 = new CloseAccountLulo(reason, amount, currency, new TargetAccount(targetPhonePrefix, targetPhone, targetName,
                targetAccount, targetAccountHolder), deviceFingerPrint);
        closeAccountLulo2 = new CloseAccountLulo(reason, amount, currency, new TargetAccount(targetPhonePrefix, targetPhone, targetName,
                targetAccount, targetAccountHolder), deviceFingerPrint);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(closeAccountLulo1, closeAccountLulo2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(closeAccountLulo1.hashCode(), closeAccountLulo2.hashCode());
    }
}