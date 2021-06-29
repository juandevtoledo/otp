package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CloseAccountOfficeWithdrawalTest {
    private CloseAccountOfficeWithdrawal closeAccountOfficeWithdrawal1;
    private CloseAccountOfficeWithdrawal closeAccountOfficeWithdrawal2;

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

        closeAccountOfficeWithdrawal1 = new CloseAccountOfficeWithdrawal(amount, reason, gmf,
                new ClaimOffice(new ClaimOffice.City(claimCityCode, claimCity, zoneCode),
                        new ClaimOffice.Office(claimOfficeCode, claimOffice, claimOfficeAddress)), deviceFingerPrint);
        closeAccountOfficeWithdrawal2 = new CloseAccountOfficeWithdrawal(amount, reason, gmf,
                new ClaimOffice(new ClaimOffice.City(claimCityCode, claimCity, zoneCode),
                        new ClaimOffice.Office(claimOfficeCode, claimOffice, claimOfficeAddress)), deviceFingerPrint);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(closeAccountOfficeWithdrawal1, closeAccountOfficeWithdrawal2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(closeAccountOfficeWithdrawal1.hashCode(), closeAccountOfficeWithdrawal2.hashCode());
    }
}