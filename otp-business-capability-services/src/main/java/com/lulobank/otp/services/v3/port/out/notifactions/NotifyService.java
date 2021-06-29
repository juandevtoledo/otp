package com.lulobank.otp.services.v3.port.out.notifactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;

import java.util.Map;

public interface NotifyService {

    Future<List<Try<Boolean>>> notify(Map<String, String> authHeaders, String otp, String clientId,
                                      OTPTransaction transactionType, JsonNode  payload);

    void notifyAuthorizationSac(String otp, ClientNotifyInfo clientNotifyInfo, String message);

}
