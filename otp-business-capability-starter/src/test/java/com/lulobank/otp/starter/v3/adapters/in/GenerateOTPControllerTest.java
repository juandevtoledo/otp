package com.lulobank.otp.starter.v3.adapters.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.services.v3.domain.DeviceFingerPrint;
import com.lulobank.otp.services.v3.domain.OTPChannel;
import com.lulobank.otp.services.v3.domain.OTPGenerationRs;
import com.lulobank.otp.services.v3.domain.OTPTransaction;
import com.lulobank.otp.services.v3.domain.accountclosure.BankAccount;
import com.lulobank.otp.services.v3.domain.accountclosure.ClaimOffice;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountCashier;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountDonation;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountInterbank;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountLulo;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountOfficeWithdrawal;
import com.lulobank.otp.services.v3.domain.accountclosure.CloseAccountSimpleCeroBalance;
import com.lulobank.otp.services.v3.domain.accountclosure.TargetAccount;
import com.lulobank.otp.services.v3.domain.clients.UpdateAddress;
import com.lulobank.otp.services.v3.domain.transactions.Credit;
import com.lulobank.otp.services.v3.domain.transactions.Geolocation;
import com.lulobank.otp.services.v3.domain.transactions.Transfer;
import com.lulobank.otp.services.v3.domain.transactions.TransferLuloReferral;
import com.lulobank.otp.services.v3.domain.transactions.TransferP2P;
import com.lulobank.otp.services.v3.domain.transactions.TransferSource;
import com.lulobank.otp.services.v3.domain.transactions.TransferTarget;
import com.lulobank.otp.services.v3.port.in.GenerateOTP;
import io.vavr.control.Try;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;

import static com.lulobank.otp.starter.v3.adapters.Sample.buildTransferLulo;
import static com.lulobank.otp.starter.v3.adapters.Sample.buildUpdateChannelLimitsRequest;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class GenerateOTPControllerTest {

    private static final String AUTHZ_TOKEN = "adfasdfwefasdvsdvdafvadfsdf";
    private static final String MOCK_OTP = "123456";
    private static final String ID_CLIENT = "234-2345-456-4567-67";
    private static final String TARGET_ACCOUNT = "011722181901";
    private static final String TARGET_ACCOUNT_HOLDER = "10323756987";
    private static final String TARGET_PHONE_PREFIX = "57";
    private static final String TARGET_PHONE = "30021456897";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(35000.00);
    private static final BigDecimal GMF = BigDecimal.valueOf(140.00);
    private static final String CURRENCY = "COP";
    private static final String REASON = "ACCOUNT CLOSURE";
    private static final String TARGET_DOCUMENT_TYPE = "CC";
    private static final String TARGET_NAME = "Herschel Shmoikel";
    private static final String BANK_ID = "1001";
    private static final String TARGET_ACCOUNT_TYPE = "ahorro";
    private static final String TARGET_BANK_DESCRIPTION = "Banco GNB Sudameris";
    private static final String DONATION_ID = "cad202ae-70a4-46a9-9a8e-e51e28e6e17e";
    private static final String DONATION_DRESCRIPTION = "Cierre por donacion";
    private static final String CLAIM_CITY_CODE = "12000";
    private static final String CLAIM_CITY = "Bogota";
    private static final String CLAIM_OFFICE_CODE = "1234";
    private static final String CLAIM_OFFICE = "Monster Mall";
    private static final String CLAIM_OFFICE_ADDRESS = "Calle falsa 1234";
    private static final String ADDRESS = "address";
    private static final String ADDRESS_PREFIX = "addressPrefix";
    private static final String ADDRESS_COMPLEMENT = "addressComplement";
    private static final String CITY = "city";
    private static final String CITY_ID = "cityId";
    private static final String DEPARTMENT = "department";
    private static final String DEPARTMENT_ID = "departmentId";
    private static final String CODE = "code";
    public static final String ZONE_CODE = "11001";

    @Mock
    private GenerateOTP generateOTP;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GenerateOTPController generateOTPController;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(generateOTPController);
    }


    @Test
    public void shouldReturn200OKWhenRequestingOTPforCREDIT() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4,60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.CREDIT,
                mapper.convertValue(new Credit("", 0.0, "", 1), JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());

    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforTRANSFER() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60,"");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.TRANSFER,
                mapper.convertValue(new Transfer("adsfasdf", 0.0), JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforUPDATE_ADDRESS() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.UPDATE_ADDRESS,
                mapper.convertValue(new UpdateAddress(ADDRESS,ADDRESS_PREFIX,ADDRESS_COMPLEMENT,CITY,CITY_ID,DEPARTMENT,DEPARTMENT_ID,CODE),
                        JsonNode.class),
                bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforUPDATE_CHANNEL_LIMITS() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.UPDATE_CHANNEL_LIMITS,
                mapper.convertValue(buildUpdateChannelLimitsRequest(), JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforTRANSFERENCIAS_P2P() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.TRANSFERENCIAS_P2P,
                mapper.convertValue(buildTransferP2P(), JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforTRANSFERENCIAS_LULO() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.TRANSFERENCIAS_LULO,
                mapper.convertValue(buildTransferLulo(), JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforINTERBANCARIAS() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.INTERBANCARIAS,
                mapper.convertValue(buildTransferLulo(), JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturnOK_otpType_INTERBANCARIAS() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.CLOSE_ACCOUNT_INTERBANK,
                mapper.convertValue(new CloseAccountInterbank(AMOUNT, GMF, CURRENCY, REASON,
                                new CloseAccountInterbank.TransferTarget(TARGET_ACCOUNT_HOLDER, TARGET_DOCUMENT_TYPE, TARGET_NAME, TARGET_PHONE,
                                        TARGET_PHONE_PREFIX,
                                        new BankAccount(BANK_ID, TARGET_ACCOUNT, TARGET_ACCOUNT_TYPE, TARGET_BANK_DESCRIPTION)), buildDeviceFingerPrint()),
                        JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforCLOSE_ACCOUNT_DONATION() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.CLOSE_ACCOUNT_DONATION,
                mapper.convertValue(new CloseAccountDonation(AMOUNT, GMF, REASON, new CloseAccountDonation.DonationEntity(DONATION_ID,
                                DONATION_DRESCRIPTION), buildDeviceFingerPrint()),
                        JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforCLOSE_ACCOUNT_CASHIERCHECK() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.CLOSE_ACCOUNT_CASHIERCHECK,
                mapper.convertValue(new CloseAccountCashier(AMOUNT, GMF, REASON,
                        new ClaimOffice(new ClaimOffice.City(CLAIM_CITY_CODE, CLAIM_CITY, ZONE_CODE),
                                new ClaimOffice.Office(CLAIM_OFFICE_CODE, CLAIM_OFFICE, CLAIM_OFFICE_ADDRESS)), buildDeviceFingerPrint()),
                        JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforCLOSE_ACCOUNT_SIMPLE_CERO_BALANCE() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.CLOSE_ACCOUNT_SIMPLE_CERO_BALANCE,
                mapper.convertValue(new CloseAccountSimpleCeroBalance(TARGET_ACCOUNT, REASON, buildDeviceFingerPrint()),
                        JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforCLOSE_ACCOUNT_OFFICE_WITHDRAWAL() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.CLOSE_ACCOUNT_OFFICE_WITHDRAWAL,
                mapper.convertValue(new CloseAccountOfficeWithdrawal(AMOUNT, REASON, GMF,
                                new ClaimOffice(new ClaimOffice.City(CLAIM_CITY_CODE, CLAIM_CITY, ZONE_CODE),
                                        new ClaimOffice.Office(CLAIM_OFFICE_CODE, CLAIM_OFFICE, CLAIM_OFFICE_ADDRESS)), buildDeviceFingerPrint()),
                        JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforCLOSE_ACCOUNT_LULO() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.CLOSE_ACCOUNT_LULO,
                mapper.convertValue(new CloseAccountLulo(REASON, AMOUNT, CURRENCY, new TargetAccount(TARGET_PHONE_PREFIX, TARGET_PHONE, TARGET_NAME
                                , TARGET_ACCOUNT, TARGET_ACCOUNT_HOLDER),
                                buildDeviceFingerPrint()),
                        JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    @Test
    public void shouldReturn200OKWhenRequestingOTPforTRANSFERENCIAS_LULO_REFERIDOS() {
        //Given
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, AUTHZ_TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        OTPGenerationRs body = new OTPGenerationRs(OTPChannel.SMS, 4, 60, "");
        given(generateOTP.execute(any())).willReturn(Try.of(() -> MOCK_OTP));

        ResponseEntity<OTPGenerationRs> response = generateOTPController.acceptOffer(headers, ID_CLIENT, OTPTransaction.TRANSFERENCIAS_LULO_REFERIDOS,
                mapper.convertValue(buildTransferLuloReferral(), JsonNode.class), bindingResult);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(body.getLength(), response.getBody().getLength());
        assertEquals(body.getChannel(), response.getBody().getChannel());
    }

    private DeviceFingerPrint buildDeviceFingerPrint() {
        return new DeviceFingerPrint(new DeviceFingerPrint.Hash("2345"),
                new DeviceFingerPrint.Geolocation(),
                new DeviceFingerPrint.DeviceDetail(),
                new DeviceFingerPrint.Coordinates());
    }

    private TransferP2P buildTransferP2P() {
        String shippingReason = "NONE";
        String sourceName = "LuloSource";
        TransferSource source1 = new TransferSource(TARGET_PHONE_PREFIX, TARGET_PHONE, sourceName,
            TARGET_ACCOUNT, TARGET_ACCOUNT_HOLDER);

        String targetName = "LuloTarget";
        TransferTarget target1 = new TransferTarget(TARGET_PHONE_PREFIX, TARGET_PHONE, targetName,
            TARGET_ACCOUNT, TARGET_ACCOUNT_HOLDER);

        return new TransferP2P(AMOUNT.doubleValue(), CURRENCY, shippingReason, source1, target1, buildDeviceFingerPrint(),
            buildGeolocation());
    }

    private TransferLuloReferral buildTransferLuloReferral() {
        return new TransferLuloReferral(new TransferSource(TARGET_PHONE_PREFIX, TARGET_PHONE, "LuloSource",
                TARGET_ACCOUNT, TARGET_ACCOUNT_HOLDER),
                new TransferTarget(TARGET_PHONE_PREFIX, TARGET_PHONE, "LuloTarget",
                        TARGET_ACCOUNT, TARGET_ACCOUNT_HOLDER),
                buildDeviceFingerPrint(),
                AMOUNT.doubleValue(),
                CURRENCY,
                "NONE"
                );
    }

    private Geolocation buildGeolocation() {
        return new Geolocation(1.234567, 2.345678);
    }
}
