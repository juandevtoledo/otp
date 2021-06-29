package com.lulobank.otp.services.v3.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountCashier;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountDonation;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountInterbank;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountLulo;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountOfficeWithdrawal;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountSimpleCeroBalance;
import com.lulobank.otp.services.v3.domain.cards.CardActivation;
import com.lulobank.otp.services.v3.domain.cards.CardInfoRequest;
import com.lulobank.otp.services.v3.domain.cards.CardRepositionRequest;
import com.lulobank.otp.services.v3.domain.cards.UnblockDebitCardRequest;
import com.lulobank.otp.services.v3.domain.clients.UpdateAddress;
import com.lulobank.otp.services.v3.domain.clients.UpdateEmail;
import com.lulobank.otp.services.v3.domain.clients.UpdateEmailAddressRequest;
import com.lulobank.otp.services.v3.domain.clients.UpdatePhoneNumber;
import com.lulobank.otp.services.v3.domain.clienttravels.CreateTravelSchedule;
import com.lulobank.otp.services.v3.domain.limits.GetChannelLimitsRequest;
import com.lulobank.otp.services.v3.domain.limits.UpdateChannelLimitsRequest;
import com.lulobank.otp.services.v3.domain.transactions.Credit;
import com.lulobank.otp.services.v3.domain.transactions.Transfer;
import com.lulobank.otp.services.v3.domain.transactions.TransferApproval;
import com.lulobank.otp.services.v3.domain.transactions.TransferInterbank;
import com.lulobank.otp.services.v3.domain.transactions.TransferLulo;
import com.lulobank.otp.services.v3.domain.transactions.TransferLuloReferral;
import com.lulobank.otp.services.v3.domain.transactions.TransferP2P;
import io.vavr.collection.List;
import io.vavr.control.Try;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

import static com.lulobank.otp.services.v3.domain.OTPChannel.MAIL;
import static com.lulobank.otp.services.v3.domain.OTPChannel.SMS;
import static com.lulobank.otp.services.v3.domain.OTPTransactionData.TransactionTypeMapper.transactionDataMapper;
import static java.util.concurrent.TimeUnit.MINUTES;


public enum OTPTransaction {

    TRANSFER(Transfer.class, SMS, MAIL),

    CREDIT(Credit.class, SMS),

    UPDATE_ADDRESS(UpdateAddress.class, SMS),

    UPDATE_CHANNEL_LIMITS(UpdateChannelLimitsRequest.class, SMS),

    TRANSFERENCIAS_LULO(TransferLulo.class, SMS, MAIL),

    TRANSFERENCIAS_P2P(TransferP2P.class, SMS, MAIL),

    INTERBANCARIAS(TransferInterbank.class, SMS, MAIL),

    CARD_ACTIVATION(CardActivation.class, SMS, MAIL),

    CARD_INFO(CardInfoRequest.class, SMS, MAIL),

    CLOSE_ACCOUNT_SIMPLE_CERO_BALANCE(CloseAccountSimpleCeroBalance.class, SMS, MAIL),

    CLOSE_ACCOUNT_INTERBANK(CloseAccountInterbank.class, SMS, MAIL),

    CLOSE_ACCOUNT_DONATION(CloseAccountDonation.class, SMS, MAIL),

    CLOSE_ACCOUNT_CASHIERCHECK(CloseAccountCashier.class, SMS, MAIL),

    CLOSE_ACCOUNT_OFFICE_WITHDRAWAL(CloseAccountOfficeWithdrawal.class, SMS, MAIL),

    CLOSE_ACCOUNT_LULO(CloseAccountLulo.class, SMS, MAIL),

    UPDATE_EMAIL(UpdateEmail.class, MAIL),

    GET_CHANNEL_LIMITS(GetChannelLimitsRequest.class, SMS),

    CARD_REPOSITION(CardRepositionRequest.class, SMS, MAIL),

    UNBLOCK_DEBIT_CARD(UnblockDebitCardRequest.class, SMS),

    CREATE_TRAVEL_SCHEDULE(CreateTravelSchedule.class, SMS, MAIL),

    APPROVE_TRANSFER(TransferApproval.class, SMS, MAIL),

    TRANSFERENCIAS_LULO_REFERIDOS(TransferLuloReferral.class, SMS, MAIL),

    UPDATE_PHONE_NUMBER(UpdatePhoneNumber.class, SMS),

    UPDATE_EMAIL_ADDRESS(UpdateEmailAddressRequest.class, SMS, MAIL);

    // ADD NEW TRANSACTIONS UP HERE /\

    /**
     * Default values constructor
     * Use this constructor when the transaction to be added it's OK with the default values for the otpLength = 4
     * otpExpirationTime = 1 Minute
     */
    OTPTransaction(Class<? extends OTPTransactionData> payloadType, OTPChannel... channels) {
        this(payloadType, 4, 1, MINUTES, channels);
    }

    /**
     * Use this constructor when the transaction to be added requires different values than the default ones for
     * otpLength and otpExpirationTime
     */
    OTPTransaction(Class<? extends OTPTransactionData> payloadType, int otpLength, int otpExpiration, TimeUnit otpExpirationUnits, OTPChannel... channels) {
        this.payloadType = payloadType;
        this.arrangement = OTPArrangement.builder()
                .length(otpLength)
                .expirationTime(otpExpiration)
                .timeUnit(otpExpirationUnits)
                .targets(List.of(channels))
                .build();
    }

    @Getter
    private OTPArrangement arrangement;
    private Class<? extends OTPTransactionData> payloadType;

    public Try<? extends OTPTransactionData> toTransactionData(JsonNode payload) {
        return transactionDataMapper(payload, payloadType);
    }
}
