package com.lulobank.otp.services.v3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.domain.deleteotptoken.DeleteOTPCardlessWithdrawalRequest;
import com.lulobank.otp.services.v3.domain.deleteotptoken.WithdrawalOperation;
import com.lulobank.otp.services.v3.domain.transactions.TransferInterbank;
import com.lulobank.otp.services.v3.domain.transactions.TransferLulo;
import com.lulobank.otp.services.v3.domain.zendesk.AuthorizationSac;
import com.lulobank.otp.services.v3.domain.zendesk.TransactionTypeSac;
import com.lulobank.otp.services.v3.domain.zendesk.VerifyAuthorizationSac;
import io.vavr.control.Try;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.lulobank.otp.services.Constants.AUTHORIZATION_HEADER;
import static com.lulobank.otp.services.Constants.EMAIL;
import static com.lulobank.otp.services.Constants.ID_CARD;
import static com.lulobank.otp.services.Constants.OTP_NUMBER;
import static com.lulobank.otp.services.Constants.PHONE_NUMBER;
import static com.lulobank.otp.services.Constants.PHONE_PREFIX;
import static com.lulobank.otp.services.Constants.PRODUCT_NUMBER;

public class Sample {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Sample() {
    }

    public static ClientNotifyInfo getClientNotifyInfo() {
        ClientNotifyInfo clientNotifyInfo = new ClientNotifyInfo();
        clientNotifyInfo.setEmail(EMAIL);
        clientNotifyInfo.setPhone(PHONE_NUMBER);
        clientNotifyInfo.setPrefix(PHONE_PREFIX);
        return clientNotifyInfo;
    }


    public static AuthorizationSac getAuthorizationSac() {
        AuthorizationSac authorizationSac = new AuthorizationSac();
        authorizationSac.setAgentId("1");
        authorizationSac.setProductNumber(PRODUCT_NUMBER);
        authorizationSac.setTransactionType(TransactionTypeSac.LOCK_ACCOUNT_SAC.name());
        authorizationSac.setClientId(ID_CARD);
        authorizationSac.setHttpHeaders(getHeaders());
        return authorizationSac;
    }

    public static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, "token");
        return headers;
    }

    public static VerifyAuthorizationSac getVerifyAuthorizationSac() {
        VerifyAuthorizationSac authorizationSac = new VerifyAuthorizationSac();
        authorizationSac.setAgentId("1");
        authorizationSac.setProductNumber(PRODUCT_NUMBER);
        authorizationSac.setTransactionType(TransactionTypeSac.LOCK_ACCOUNT_SAC.name());
        authorizationSac.setClientId(ID_CARD);
        authorizationSac.setOtp(OTP_NUMBER);
        return authorizationSac;
    }

    public static DeleteOTPCardlessWithdrawalRequest buildDeleteOTPCardlessWithdrawalRequest() {
        return DeleteOTPCardlessWithdrawalRequest.builder()
                .otp(OTP_NUMBER)
                .withdrawalOperation(WithdrawalOperation.builder()
                        .withdrawal(WithdrawalOperation.Withdrawal.builder()
                                .documentId(ID_CARD)
                                .amount(BigInteger.valueOf(10000))
                                .build())
                        .build())
                .build();
    }

    public static TransferLulo buildTransferLulo() {
        return deserializeResource("data/v3/domain/payloads/TransferLulo.json", TransferLulo.class);
    }

    public static TransferInterbank buildTransferInterbank() {
        return deserializeResource("data/v3/domain/payloads/TransferInterbank.json", TransferInterbank.class);
    }

    private static <T> T deserializeResource(String resourcePath, Class<T> targetType) {
        return Try.success(resourcePath)
                .map(Resources::getResource)
                .mapTry(url -> Resources.toString(url, StandardCharsets.UTF_8))
                .mapTry(resourceText -> objectMapper.readValue(resourceText, targetType))
                .get();
    }
}
