package com.lulobank.otp.services.v3.domain.clients;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpdateAddressTest {

    private UpdateAddress updateAddress1;
    private UpdateAddress updateAddress2;

    @Before
    public void init() {

        String address = "address";
        String addressPrefix = "addressPrefix";
        String addressComplement = "addressComplement";
        String city = "city";
        String cityId = "cityId";
        String department = "department";
        String departmentId = "departmentId";
        String code = "code";

        updateAddress1 = new UpdateAddress(address,addressPrefix,addressComplement,city,cityId,department,departmentId,code);
        updateAddress2 = new UpdateAddress(address,addressPrefix,addressComplement,city,cityId,department,departmentId,code);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(updateAddress1, updateAddress2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(updateAddress1.hashCode(), updateAddress2.hashCode());
    }

}
