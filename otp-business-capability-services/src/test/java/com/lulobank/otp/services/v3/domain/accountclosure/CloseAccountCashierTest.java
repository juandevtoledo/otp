package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CloseAccountCashierTest {

    private CloseAccountCashier closeAccountCashier1;
    private CloseAccountCashier closeAccountCashier2;

    @Before
    public void init() {

        BigDecimal amount = BigDecimal.valueOf(35000.00);
        BigDecimal gmf = BigDecimal.valueOf(140.00);
        String reason = "ACCOUNT CLOSURE";
        String claimCityCode = "12000";
        String claimCity = "Bogota";
        String claimOfficeCode = "1234";
        String claimOffice = "Monster Mall";
        String claimOfficeAddress = "Calle falsa 1234";
        String zoneCode = "11001";
        DeviceFingerPrint deviceFingerPrint = new DeviceFingerPrint();
        deviceFingerPrint.setHash(new DeviceFingerPrint.Hash("1234"));

        closeAccountCashier1 = new CloseAccountCashier(amount, gmf, reason,
                new ClaimOffice(new ClaimOffice.City(claimCityCode, claimCity, zoneCode),
                        new ClaimOffice.Office(claimOfficeCode, claimOffice, claimOfficeAddress)), deviceFingerPrint);

        closeAccountCashier2 = new CloseAccountCashier(amount, gmf, reason,
                new ClaimOffice(new ClaimOffice.City(claimCityCode, claimCity, zoneCode),
                        new ClaimOffice.Office(claimOfficeCode, claimOffice, claimOfficeAddress)), deviceFingerPrint);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(closeAccountCashier1, closeAccountCashier2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(closeAccountCashier1.hashCode(), closeAccountCashier2.hashCode());
    }

}