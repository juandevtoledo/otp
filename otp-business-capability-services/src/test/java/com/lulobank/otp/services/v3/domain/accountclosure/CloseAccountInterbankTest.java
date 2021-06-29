package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CloseAccountInterbankTest {

    private CloseAccountInterbank closeAccountInterbank1;
    private CloseAccountInterbank closeAccountInterbank2;

    @Before
    public void init() {
        BigDecimal amount = BigDecimal.valueOf(35000.00);
        BigDecimal gmf = BigDecimal.valueOf(140.00);
        String currency = "COP";
        String reason = "ACCOUNT CLOSURE";
        String targetAccountHolder = "10323756987";
        String targetDocumentType = "CC";
        String targetName = "Herschel Shmoikel";
        String bankId = "1001";
        String targetAccountType = "ahorro";
        String targetBankDescription = "Banco GNB Sudameris";
        String targetPhonePrefix = "57";
        String targetPhone = "30021456897";
        String targetAccount = "011722181901";
        DeviceFingerPrint deviceFingerPrint = new DeviceFingerPrint();
        deviceFingerPrint.setHash(new DeviceFingerPrint.Hash("1234"));

        closeAccountInterbank1 = new CloseAccountInterbank(amount, gmf, currency, reason,
                new CloseAccountInterbank.TransferTarget(targetAccountHolder, targetDocumentType, targetName, targetPhone,
                        targetPhonePrefix, new BankAccount(bankId, targetAccount, targetAccountType, targetBankDescription)), deviceFingerPrint);

        closeAccountInterbank2 = new CloseAccountInterbank(amount, gmf, currency, reason,
                new CloseAccountInterbank.TransferTarget(targetAccountHolder, targetDocumentType, targetName, targetPhone,
                        targetPhonePrefix, new BankAccount(bankId, targetAccount, targetAccountType, targetBankDescription)), deviceFingerPrint);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(closeAccountInterbank1, closeAccountInterbank2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(closeAccountInterbank1.hashCode(), closeAccountInterbank2.hashCode());
    }

}