package com.lulobank.otp.starter.v3.adapters.out.clients.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientInformationByIdCard {
    private String idClient;
    private String name;
    private String lastName;
    private Integer phonePrefix;
    private String phoneNumber;
    private String emailAddress;
    private String idCbs;
    private String idCard;
}
