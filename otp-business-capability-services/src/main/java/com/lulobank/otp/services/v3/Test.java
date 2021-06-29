package com.lulobank.otp.services.v3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.otp.services.v3.domain.transactions.TransferInterbank;

public class Test {

    public static void main(String... args){
        String payload =" {\n" +
                "  \"amount\": 10000,\n" +
                "  \"shippingReason\": \"NONE\",\n" +
                "  \"source\": {\n" +
                "    \"sourceAccount\": \"892665717871\",\n" +
                "    \"phone\": \"3002000073\",\n" +
                "    \"sourceAccountHolder\": \"86960073\",\n" +
                "    \"prefix\": 57,\n" +
                "    \"name\": \"WYMAN QA ERDMAN LOGOS\"\n" +
                "  },\n" +
                "  \"target\": {\n" +
                "    \"phone\": \"3206277848\",\n" +
                "    \"prefix\": \"57\",\n" +
                "    \"documentType\": \"IDCARD\",\n" +
                "    \"externalBankAccount\": {\n" +
                "      \"bankDescription\": \"BANCO DE BOGOTA\",\n" +
                "      \"account\": \"05912663223\",\n" +
                "      \"accountEnrollment\": true,\n" +
                "      \"bankId\": \"1001\",\n" +
                "      \"accountType\": \"ahorro\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"deviceFingerPrint\": {\n" +
                "    \"hash\": {\n" +
                "      \"id\": \"83188458738d8dbf7260d6d24db267f4f7ced53b098ccf85234687c8259c55bc\"\n" +
                "    },\n" +
                "    \"general\": {\n" +
                "      \"deviceId\": \"92307F89-08AA-4C3A-A228-BDCA5FD9C842\",\n" +
                "      \"hostname\": \"iPhone 11 Pro Max\",\n" +
                "      \"passiveId\": \"No Disponible\",\n" +
                "      \"macAddress\": \"No Disponible\"\n" +
                "    },\n" +
                "    \"geolocation\": {\n" +
                "      \"country\": \"Colombia\",\n" +
                "      \"isp\": \"\",\n" +
                "      \"city\": \"Bogot\\\\303\\\\241\",\n" +
                "      \"ip\": \"190.84.196.81\"\n" +
                "    },\n" +
                "    \"coordinates\": {\n" +
                "      \"longitude\": -122.406417,\n" +
                "      \"latitude\": 37.785834\n" +
                "    }\n" +
                "  },\n" +
                "  \"geoLocation\": {\n" +
                "    \"longitude\": -122.406417,\n" +
                "    \"latitude\": 37.785834\n" +
                "  }\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            System.out.println(payload);
            actualObj = mapper.readTree(payload);
            TransferInterbank t = mapper.treeToValue(actualObj, TransferInterbank.class);
            System.out.println(t.getAmount());
        } catch (Exception e) {
            System.out.println("e "+e.getMessage());
            e.printStackTrace();
        }

    }

}
