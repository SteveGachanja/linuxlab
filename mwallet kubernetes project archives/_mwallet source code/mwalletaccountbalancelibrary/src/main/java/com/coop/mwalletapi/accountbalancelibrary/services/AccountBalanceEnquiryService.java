package com.coop.mwalletapi.accountbalancelibrary.services;

import com.coop.mwalletapi.accountbalancelibrary.dao.Db;
import com.coop.mwalletapi.accountbalancelibrary.dao.model.AccountBalanceEnquiryDataDbResp;
import com.coop.mwalletapi.accountbalancelibrary.entities.AccountBalanceEnquiryResponse;
import com.coop.mwalletapi.accountbalancelibrary.entities.InputAccountRequest;
import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import com.coop.mwalletapi.functions.Functions;
import com.coop.mwalletapi.kafka.KafkaLogProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author pkingongo
 */
public class AccountBalanceEnquiryService {

    private static final Logger logger = LogManager.getLogger(AccountBalanceEnquiryService.class.getName());

    public Object getAccountBalanceByAccountAlias(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        AccountBalanceEnquiryResponse accountResp = new AccountBalanceEnquiryResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        InputAccountRequest inputAccountRequest = new InputAccountRequest();
        KafkaLogProducer kafkaProducer = new KafkaLogProducer(commonDataReq.getKafkaConfigs().getServer(), commonDataReq.getKafkaConfigs().getClientId(), commonDataReq.getKafkaConfigs().getTopic());

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(AccountBalanceEnquiryService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            inputAccountRequest = mapper.readValue(myCustomerJson, InputAccountRequest.class);

            accountResp.setMessageId(inputAccountRequest.getMessageId());
            //validate credentials
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), inputAccountRequest.getApiUser(), inputAccountRequest.getApiPassword());
            if (validApiUser <= 0) {
                accountResp.setResponseCode(1);
                accountResp.setResponseMessage("API User not allowed");
                accountResp.setMessageId(inputAccountRequest.getMessageId());
                accountResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(accountResp);
                return retObj;
            }
            if (!validateDateTime(inputAccountRequest.getRequestDateTime())) {
                accountResp.setResponseCode(1);
                accountResp.setResponseMessage("Check the Request Date");
                accountResp.setMessageId(inputAccountRequest.getMessageId());
                accountResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(accountResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), inputAccountRequest.getRequestDateTime())) {
                    accountResp.setResponseCode(1);
                    accountResp.setResponseMessage("Check the Request Date Must be today");
                    accountResp.setMessageId(inputAccountRequest.getMessageId());
                    accountResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(accountResp);
                    return retObj;
                }
            }

            //Do the processing here
            //validations if any
            if (func.isNullOrEmpty(inputAccountRequest.getRequestBody().getAccountAlias())) {
                accountResp.setResponseCode(1);
                accountResp.setResponseMessage("Check the account alias");

                accountResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(accountResp);
                return retObj;
            }

            Db db = new Db(commonDataReq.getMwalletDataSource());
            AccountBalanceEnquiryDataDbResp accountDataDbResp = db.getMClientData(inputAccountRequest.getRequestBody().getAccountAlias(), validApiUser);

            if (accountDataDbResp == null) {
                accountResp.setResponseCode(1);
                accountResp.setResponseMessage("Internal Server error when fetching from db.");
            } else {
                accountDataDbResp.setMessageId(inputAccountRequest.getMessageId());
                accountResp.setResponseCode(accountDataDbResp.getResponseCode());
                accountResp.setResponseMessage(accountDataDbResp.getResponseMessage());
                accountResp.setResponseBody(accountDataDbResp);
            }

            retObj = Obj.writeValueAsString(accountResp);
            
            HashMap<String, String> mapKafkaPayload = new HashMap<>();
            final String messageId = inputAccountRequest.getMessageId();
            if (!func.isNullOrEmpty(Integer.toString(accountResp.getResponseCode()))) {
                if (accountResp.getResponseCode() == 0) {
                    if (!func.isNullOrEmpty(accountResp.getResponseMessage())) {
                        mapKafkaPayload.put("SessionId", inputAccountRequest.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", inputAccountRequest.getMessageId());
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "GET_BALANCE");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", Integer.toString(accountResp.getResponseCode()));
                        mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(accountResp.getResponseMessage()) ? "" : accountResp.getResponseMessage()));
                    } else {
                        mapKafkaPayload.put("SessionId", inputAccountRequest.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "GET_BALANCE");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                    }
                } else {
                    mapKafkaPayload.put("SessionId", inputAccountRequest.getMessageId());
                    mapKafkaPayload.put("ReferenceNumber", "");
                    mapKafkaPayload.put("MobileNumber", "");
                    mapKafkaPayload.put("RequestType", "GET_BALANCE");
                    mapKafkaPayload.put("Amount", "0");
                    mapKafkaPayload.put("ResponseCode", Integer.toString(accountResp.getResponseCode()));
                    mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(accountResp.getResponseMessage()) ? "" : accountResp.getResponseMessage()));
                }
            } else {
                mapKafkaPayload.put("SessionId", inputAccountRequest.getMessageId());
                mapKafkaPayload.put("ReferenceNumber", "");
                mapKafkaPayload.put("MobileNumber", "");
                mapKafkaPayload.put("RequestType", "GET_BALANCE");
                mapKafkaPayload.put("Amount", "0");
                mapKafkaPayload.put("ResponseCode", "1");
                mapKafkaPayload.put("ResponseDescription", "Failed");
            }

            Thread kafkaLog = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(0);
                        kafkaProducer.sendMessage("CustomersLibrary", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                    } catch (InterruptedException v) {
                        logger.error(AccountBalanceEnquiryService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                    }
                }
            };
            kafkaLog.start();

        } catch (Exception ex) {
            logger.error(AccountBalanceEnquiryService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            accountResp.setResponseCode(0);
            accountResp.setResponseMessage("Internal Server error.");
            accountResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(accountResp);
        }
        return retObj;
    }

    static List<String> formatStrings = Arrays.asList(
            "yyyy-MM-dd HH:mm:ss.SSS Z",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-ddTHH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:SSSZ",
            "yyyy-MM-dd HH:mm:SSS",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyyMMddHHmmss",
            "dd-MM-yyyy",
            "dd/MM/yyyy",
            "MM/dd/yyyy"
    );

    public static boolean validateDateTime(String dateIn) {
        for (String formatString : formatStrings) {
            try {
//                System.out.println(formatString + " - " + dateIn);
                Date datetime = new SimpleDateFormat(formatString).parse(dateIn);
                return true;
            } catch (ParseException e) {
            }
        }
        return false;

    }
}
