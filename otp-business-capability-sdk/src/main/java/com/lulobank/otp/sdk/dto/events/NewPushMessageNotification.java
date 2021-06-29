package com.lulobank.otp.sdk.dto.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lulobank.core.Command;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewPushMessageNotification  implements Command {
    private  String idClient;
    private  String idNotification;
    private  String arnSns;
    private  String title;
    private  String messageBody;
    private  String osDevice;
    private  String action;
}
