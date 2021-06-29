package com.lulobank.otp.services.v3.domain.transactions;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TransferP2PTest {
    private TransferP2P transferP2P1;
    private TransferP2P transferP2P2;

    @Before
    public void init() {
        Double amount = 10_000D;
        String currency = "COP";
        String shippingReason = "NONE";

        String sourcePrefix = "57";
        String sourcePhone = "3101234567";
        String sourceName = "LuloSource";
        String sourceAccount = "0001234567890";
        String sourceAccountHolder = "1234567890";
        TransferSource source1 = new TransferSource(sourcePrefix, sourcePhone, sourceName,
            sourceAccount, sourceAccountHolder);
        TransferSource source2 = new TransferSource(sourcePrefix, sourcePhone, sourceName,
            sourceAccount, sourceAccountHolder);

        String prefix = "57";
        String phone = "3107654321";
        String name = "LuloTarget";
        TransferTarget target1 = new TransferTarget(prefix, phone, name, null, null);
        TransferTarget target2 = new TransferTarget(prefix, phone, name, null, null);

        String hash = UUID.randomUUID().toString();
        DeviceFingerPrint deviceFingerPrint1 = new DeviceFingerPrint();
        deviceFingerPrint1.setHash(new DeviceFingerPrint.Hash(hash));
        DeviceFingerPrint deviceFingerPrint2 = new DeviceFingerPrint();
        deviceFingerPrint2.setHash(new DeviceFingerPrint.Hash(hash));

        Double lat = 1.234567;
        Double lon = 2.345678;
        Geolocation geolocation1 = new Geolocation(lat, lon);
        Geolocation geolocation2 = new Geolocation(lat, lon);

        transferP2P1 = new TransferP2P(amount, currency, shippingReason, source1, target1, deviceFingerPrint1, geolocation1);
        transferP2P2 = new TransferP2P(amount, currency, shippingReason, source2, target2, deviceFingerPrint2, geolocation2);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(transferP2P1, transferP2P2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(transferP2P1.hashCode(), transferP2P2.hashCode());
    }
}