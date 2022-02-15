package com.coop.mwalletapi.accountstatementlibrary.services;

import com.coop.mwalletapi.accountstatementlibrary.dao.DbV3;
import com.coop.mwalletapi.accountstatementlibrary.dao.model.AccountStatementDataDbResponse;
import com.coop.mwalletapi.accountstatementlibrary.entities.AccountStatementResponse;
import com.coop.mwalletapi.accountstatementlibrary.entities.v3.AccountStatementRequest;
import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import com.coop.mwalletapi.functions.Functions;
import com.coop.mwalletapi.kafka.KafkaLogProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author okahia
 */

public class AccountStatementVthreeService {

    private static final Logger logger = LogManager.getLogger(AccountStatementVthreeService.class.getName());

    public Object getAccountStatement(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        AccountStatementResponse response = new AccountStatementResponse();
        response.setResponseCode(1);
        response.setResponseMessage("Processing");

        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        AccountStatementRequest inputChargesRequest = new AccountStatementRequest();
        KafkaLogProducer kafkaProducer = new KafkaLogProducer(commonDataReq.getKafkaConfigs().getServer(), commonDataReq.getKafkaConfigs().getClientId(), commonDataReq.getKafkaConfigs().getTopic());

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(AccountStatementVthreeService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            inputChargesRequest = mapper.readValue(myCustomerJson, AccountStatementRequest.class);

            response.setMessageId(inputChargesRequest.getMessageId());
            //validate credentials
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), inputChargesRequest.getApiUser(), inputChargesRequest.getApiPassword());
            if (validApiUser <= 0) {
                response.setResponseCode(1);
                response.setResponseMessage("API user not authorised.");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }
            //validations if any
            if (!func.validateDateTime(inputChargesRequest.getRequestDateTime())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Request Date");
                response.setMessageId(inputChargesRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), inputChargesRequest.getRequestDateTime())) {
                    response.setResponseCode(1);
                    response.setResponseMessage("Check the Request Date Must be today");
                    response.setMessageId(inputChargesRequest.getMessageId());
                    response.setResponseBody(null);
                    retObj = Obj.writeValueAsString(response);
                    return retObj;
                }
            }
           
            int pageNums = 0;
            try
            {
                if (!func.isNullOrEmpty(Integer.toString(inputChargesRequest.getRequestBody().getPageNumber()))){
                    pageNums = inputChargesRequest.getRequestBody().getPageNumber();
                }
            } catch (Exception exx) {
                pageNums = 0;
            }

            DbV3 db = new DbV3(commonDataReq.getMwalletDataSource());
            AccountStatementDataDbResponse db_response = db.getAccountStatment(
                    inputChargesRequest.getRequestBody().getAccountAlias(), validApiUser, 
                    inputChargesRequest.getRequestBody().getTransactionId(), 
                    inputChargesRequest.getRequestBody().getMaxRows(),
                    pageNums,
                    inputChargesRequest.getRequestBody().getStartDate(), 
                    inputChargesRequest.getRequestBody().getEndDate(), 
                    response
            );
            response.setResponseBody(db_response);

            retObj = Obj.writeValueAsString(response);
            HashMap<String, String> mapKafkaPayload = new HashMap<>();
            final String messageId = inputChargesRequest.getMessageId();
            if (!func.isNullOrEmpty(Integer.toString(response.getResponseCode()))) {
                if (response.getResponseCode() == 0) {
                    if (!func.isNullOrEmpty(response.getResponseMessage())) {
                        mapKafkaPayload.put("SessionId", inputChargesRequest.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", inputChargesRequest.getMessageId());
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "GET_ACCOUNT_STATEMENT");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", Integer.toString(response.getResponseCode()));
                        mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(response.getResponseMessage()) ? "" : response.getResponseMessage()));
                    } else {
                        mapKafkaPayload.put("SessionId", inputChargesRequest.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "GET_ACCOUNT_STATEMENT");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                    }
                } else {
                    mapKafkaPayload.put("SessionId", inputChargesRequest.getMessageId());
                    mapKafkaPayload.put("ReferenceNumber", "");
                    mapKafkaPayload.put("MobileNumber", "");
                    mapKafkaPayload.put("RequestType", "GET_ACCOUNT_STATEMENT");
                    mapKafkaPayload.put("Amount", "0");
                    mapKafkaPayload.put("ResponseCode", Integer.toString(response.getResponseCode()));
                    mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(response.getResponseMessage()) ? "" : response.getResponseMessage()));
                }
            } else {
                mapKafkaPayload.put("SessionId", inputChargesRequest.getMessageId());
                mapKafkaPayload.put("ReferenceNumber", "");
                mapKafkaPayload.put("MobileNumber", "");
                mapKafkaPayload.put("RequestType", "GET_ACCOUNT_STATEMENT");
                mapKafkaPayload.put("Amount", "0");
                mapKafkaPayload.put("ResponseCode", "1");
                mapKafkaPayload.put("ResponseDescription", "Failed");
            }

            Thread kafkaLog = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(0);
                        kafkaProducer.sendMessage("AccountStatementService", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                    } catch (InterruptedException v) {
                        logger.error(AccountStatementVthreeService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                    }
                }
            };
            kafkaLog.start();

        } catch (Exception ex) {
            logger.error(AccountStatementVthreeService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            response.setResponseCode(1);
            response.setResponseMessage("Internal Server error.");
            retObj = Obj.writeValueAsString(response);
        }
        return retObj;
    }

    
}
