package com.lulobank.otp.services.v3;

import com.lulobank.otp.services.outbounadadapters.services.AddressFactory;
import org.junit.Test;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AddressFactoryTest {


    @Test
    public void generateAddressWhenOnlyOneAddress() throws IOException, AddressException {
        Address[] of = AddressFactory.of("jorge@test.com");
        assertThat(of.length, is(equalTo(1)));
    }

    @Test
    public void generateAddressWhenManyAddresses() throws IOException, AddressException {
        Address[] of = AddressFactory.of(Arrays.asList("example@gmail.com", " example2@ypmail.com"));
        assertThat(of.length, is(equalTo(2)));
    }


}
