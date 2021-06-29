package com.lulobank.otp.starter.adapters.out.pushnotifications.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FirebaseResponse {

    public String multicast_id;
    public Boolean success;
    public Boolean failure;
    public String canonical_ids;
    public List<Result> results;


}
