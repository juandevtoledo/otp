package com.lulobank.otp.services.v3.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class DeviceFingerPrint implements OTPTransactionData {
    @Valid
    @NotNull
    private Hash hash;
    private Geolocation geolocation;
    private DeviceDetail general;
    private Coordinates coordinates;

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hash implements OTPTransactionData{
        @NotBlank
        private String id;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Geolocation implements OTPTransactionData{
        private String city;
        private String country;
        private String ip;
        private String isp;
    }
    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeviceDetail implements OTPTransactionData{
        private String deviceId;
        private String hostname;
        private String macAddress;
        private String passiveId;
    }

    @Getter
    @Setter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coordinates {
        private Double latitude;
        private Double longitude;
    }
}
