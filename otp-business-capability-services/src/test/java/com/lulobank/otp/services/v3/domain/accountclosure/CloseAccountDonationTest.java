package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class CloseAccountDonationTest {

    private CloseAccountDonation closeAccountDonation1;
    private CloseAccountDonation closeAccountDonation2;

    @Before
    public void init() {
        BigDecimal amount = BigDecimal.valueOf(35000.00);
        BigDecimal gmf = BigDecimal.valueOf(140.00);
        String reason = "ACCOUNT CLOSURE";
        String donationId = "cad202ae-70a4-46a9-9a8e-e51e28e6e17e";
        String donationDrescription = "Cierre por donacion";
        DeviceFingerPrint deviceFingerPrint = new DeviceFingerPrint();
        deviceFingerPrint.setHash(new DeviceFingerPrint.Hash("1234"));

        closeAccountDonation1 = new CloseAccountDonation(amount, gmf, reason, new CloseAccountDonation.DonationEntity(donationId,
                donationDrescription), deviceFingerPrint);

        closeAccountDonation2 = new CloseAccountDonation(amount, gmf, reason, new CloseAccountDonation.DonationEntity(donationId,
                donationDrescription), deviceFingerPrint);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(closeAccountDonation1, closeAccountDonation2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(closeAccountDonation1.hashCode(), closeAccountDonation2.hashCode());
    }

}