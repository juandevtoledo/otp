package com.lulobank.otp.services.v3.domain.clients;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpdateEmailTest {

    private UpdateEmail updateEmail1;
    private UpdateEmail updateEmail2;

    @Before
    public void init() {
        String mail = "mail@mail.com";
        updateEmail1 = new UpdateEmail(mail);
        updateEmail2 = new UpdateEmail(mail);
    }

    @Test
    public void equalityOnSameValuesInstances() {
        assertEquals(updateEmail1, updateEmail2);
    }

    @Test
    public void hashCodeOnSameValuesInstances() {
        assertEquals(updateEmail1.hashCode(), updateEmail2.hashCode());
    }

}
