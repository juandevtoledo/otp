package com.lulobank.otp.services.v3.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientNotifyInfo {

    private String email;

    private String phone;

    private Integer prefix;

}
