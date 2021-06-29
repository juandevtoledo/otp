package com.lulobank.otp.services.v3.domain.limits;

import com.lulobank.otp.services.v3.domain.OTPTransactionData;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateChannelLimitsRequest implements OTPTransactionData {

    @NotBlank
    private String product;
    @NotNull
    private List<Channel> channelsToUpdate;

    @Data
    public static class Channel {
        @NotBlank
        private String channel;
        @NotNull
        private BigDecimal actualAmount;
        @NotNull
        private BigDecimal updateAmount;
    }
}
