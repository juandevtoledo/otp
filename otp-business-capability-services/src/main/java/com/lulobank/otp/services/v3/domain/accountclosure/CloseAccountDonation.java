package com.lulobank.otp.services.v3.domain.accountclosure;

import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CloseAccountDonation implements OTPTransactionData {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal gmf;
    @NotNull
    private String reason;
    @NotNull
    private DonationEntity donationEntity;
    @Valid
    @NotNull
    private DeviceFingerPrint deviceFingerPrint;

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DonationEntity implements OTPTransactionData {
        @NotNull
        private String id;
        @NotNull
        private String description;
    }
}
