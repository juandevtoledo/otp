package com.lulobank.otp.starter.v3.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.lulobank.otp.services.v3.domain.ClientNotifyInfo;
import com.lulobank.otp.services.v3.domain.error.UseCaseResponseError;
import com.lulobank.otp.services.v3.domain.limits.UpdateChannelLimitsRequest;
import com.lulobank.otp.services.v3.domain.transactions.TransferLulo;
import com.lulobank.otp.services.v3.domain.zendesk.TransactionTypeSac;
import com.lulobank.otp.starter.v3.adapters.in.deleteotptoken.dto.DeleteOTPCardlessWithdrawalInboundRequest;
import com.lulobank.otp.starter.v3.adapters.in.sac.dto.AuthorizationSacRequest;
import com.lulobank.otp.starter.v3.adapters.in.sac.dto.VerifyAuthorizationSacRequest;
import com.lulobank.otp.starter.v3.adapters.out.clients.dto.ClientInformationByIdCard;
import com.lulobank.otp.starter.v3.adapters.out.clients.dto.ResponseClientInformationByIdCard;
import com.lulobank.otp.starter.v3.adapters.out.redis.model.OtpRedisEntity;
import io.vavr.control.Try;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.lulobank.otp.starter.v3.adapters.Constant.AUTHORIZATION_HEADER;
import static com.lulobank.otp.starter.v3.adapters.Constant.ID_CARD;
import static com.lulobank.otp.starter.v3.adapters.Constant.ID_CLIENT;
import static com.lulobank.otp.starter.v3.adapters.Constant.MAIL;
import static com.lulobank.otp.starter.v3.adapters.Constant.OTP;
import static com.lulobank.otp.starter.v3.adapters.Constant.PHONE_NUMBER;
import static com.lulobank.otp.starter.v3.adapters.Constant.PHONE_PREFIX;
import static com.lulobank.otp.starter.v3.adapters.Constant.WITHDRAWAL_AMOUNT;

public class Sample {

    private static final ObjectMapper jsonMapper = new ObjectMapper();

    public static ResponseClientInformationByIdCard getResponseClientInformationByIdCard() {
        ResponseClientInformationByIdCard responseClientInformationByIdCard = new ResponseClientInformationByIdCard();
        responseClientInformationByIdCard.setContent(getClientInformationByIdCard());
        return responseClientInformationByIdCard;
    }

    private static ClientInformationByIdCard getClientInformationByIdCard() {
        ClientInformationByIdCard clientInformationByIdCard = new ClientInformationByIdCard();
        clientInformationByIdCard.setEmailAddress(MAIL);
        clientInformationByIdCard.setIdCard(ID_CARD);
        clientInformationByIdCard.setIdCbs("24552");
        clientInformationByIdCard.setIdClient(ID_CLIENT);
        clientInformationByIdCard.setLastName("lastName");
        clientInformationByIdCard.setName("name");
        clientInformationByIdCard.setPhoneNumber(PHONE_NUMBER);
        clientInformationByIdCard.setPhonePrefix(PHONE_PREFIX);
        return clientInformationByIdCard;
    }

    public static Map<String, String> getAuthorizationHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, "token");
        return headers;
    }

    public static ClientNotifyInfo getClientNotifyInfo() {
        ClientNotifyInfo clientNotifyInfo = new ClientNotifyInfo();
        clientNotifyInfo.setPrefix(PHONE_PREFIX);
        clientNotifyInfo.setPhone(PHONE_NUMBER);
        clientNotifyInfo.setEmail(MAIL);
        return clientNotifyInfo;
    }

    public static AuthorizationSacRequest getAuthorizationSacRequest() {
        AuthorizationSacRequest authorizationSacRequest = new AuthorizationSacRequest();
        authorizationSacRequest.setAgentId("1");
        authorizationSacRequest.setClientId(ID_CARD);
        authorizationSacRequest.setProductNumber("6543");
        authorizationSacRequest.setTransactionType(TransactionTypeSac.LOCK_ACCOUNT_SAC.name());
        return authorizationSacRequest;
    }

    public static VerifyAuthorizationSacRequest getVerifyAuthorizationSacRequest() {
        VerifyAuthorizationSacRequest verifyAuthorizationSacRequest = new VerifyAuthorizationSacRequest();
        verifyAuthorizationSacRequest.setAgentId("1");
        verifyAuthorizationSacRequest.setClientId(ID_CARD);
        verifyAuthorizationSacRequest.setProductNumber("6543");
        verifyAuthorizationSacRequest.setTransactionType(TransactionTypeSac.LOCK_ACCOUNT_SAC.name());
        verifyAuthorizationSacRequest.setOtp("0000");
        return verifyAuthorizationSacRequest;
    }

    public static DeleteOTPCardlessWithdrawalInboundRequest buildDeleteOTPCardlessWithdrawalInboundRequest() {
        DeleteOTPCardlessWithdrawalInboundRequest deleteOTPCardlessWithdrawalInboundRequest = new DeleteOTPCardlessWithdrawalInboundRequest();
        deleteOTPCardlessWithdrawalInboundRequest.setOtp(OTP);
        deleteOTPCardlessWithdrawalInboundRequest.setWithdrawal(new DeleteOTPCardlessWithdrawalInboundRequest.WithdrawalRequest());
        deleteOTPCardlessWithdrawalInboundRequest.getWithdrawal().setAmount(WITHDRAWAL_AMOUNT);
        deleteOTPCardlessWithdrawalInboundRequest.getWithdrawal().setDocumentId(ID_CARD);
        return deleteOTPCardlessWithdrawalInboundRequest;
    }

    public static UseCaseResponseError buildUseCaseResponseError() {
        return new UseCaseResponseError("OTP_184", String.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    public static OtpRedisEntity buildOtpRedisEntity() {
        return new OtpRedisEntity("fdwfewe856wew69ferw8", OTP, 5);
    }

    public static UpdateChannelLimitsRequest buildUpdateChannelLimitsRequest() {
        UpdateChannelLimitsRequest.Channel channel = new UpdateChannelLimitsRequest.Channel();
        channel.setChannel("TRANSFERENCIAS_P2P");
        channel.setActualAmount(BigDecimal.valueOf(10000.0));
        channel.setUpdateAmount(BigDecimal.valueOf(20000.0));
        UpdateChannelLimitsRequest updateChannelLimitsRequest = new UpdateChannelLimitsRequest();
        updateChannelLimitsRequest.setChannelsToUpdate(Collections.singletonList(channel));
        return updateChannelLimitsRequest;
    }

    public static TransferLulo buildTransferLulo() {
        return deserializeResource("mocks/transactions/TransferLulo.json", TransferLulo.class);
    }

    private static <T> T deserializeResource(String resourcePath, Class<T> targetType) {
        return Try.success(resourcePath)
                .map(Resources::getResource)
                .mapTry(url -> Resources.toString(url, StandardCharsets.UTF_8))
                .mapTry(resourceText -> jsonMapper.readValue(resourceText, targetType))
                .get();
    }
}
