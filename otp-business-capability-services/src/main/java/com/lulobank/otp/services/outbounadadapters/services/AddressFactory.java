package com.lulobank.otp.services.outbounadadapters.services;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;

public class AddressFactory {

    public static Address[] of(String address) throws AddressException {
        return new Address[]{new InternetAddress(address)};
    }

    public static Address[] of(List<String> address) throws AddressException {
        Address[] addresses = new Address[address.size()];
        for (int i = 0; i < address.size(); i++) {
            addresses[i] = new InternetAddress(address.get(i));
        }
        return addresses;
    }
}
