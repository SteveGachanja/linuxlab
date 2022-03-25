package com.coop.mwalletapi.chargeslibrary.services;

import com.coop.mwalletapi.chargeslibrary.dao.Db;
import com.coop.mwalletapi.chargeslibrary.dao.model.ChargesEnquiryDataDbResponse;
import com.coop.mwalletapi.chargeslibrary.entities.ChargesEnquiryResponse;
import com.coop.mwalletapi.chargeslibrary.entities.InputChargesRequest;
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
 * @author cndirangu
 */
public class ChargesEnquiryService {

    private static final Logger logger = LogManager.getLogger(ChargesEnquiryService.class.getName());

    public Object getCharges(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        ChargesEnquiryResponse response = new ChargesEnquiryResponse();
        response.setResponseCode(1);
        response.setResponseMessage("Processing");

        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        InputChargesRequest inputChargesRequest = new InputChargesRequest();
        KafkaLogProducer kafkaProducer = new KafkaLogProducer(commonDataReq.getKafkaConfigs().getServer(), commonDataReq.getKafkaConfigs().getClientId(), commonDataReq.getKafkaConfigs().getTopic());

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(ChargesEnquiryService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            inputChargesRequest = mapper.readValue(myCustomerJson, InputChargesRequest.class);

            response.setMessageId(inputChargesRequest.getMessageId());
            //validate credentials
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), inputChargesRequest.getApiUser(), inputChargesRequest.getApiPassword());
            if (validApiUser <= 0) {
                response.setResponseCode(1);
                response.setResponseMessage("API user not authorised.");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }
            if (!validateDateTime(inputChargesRequest.getRequestDateTime())) {
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

            if (func.isNullOrEmpty(inputChargesRequest.getRequestBody().getTransactionCode())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the TransactionCode");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputChargesRequest.getRequestBody().getBranchCode())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the BranchCode");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputChargesRequest.getRequestBody().getProductCode())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the ProductCode");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            Db db = new Db(commonDataReq.getMwalletDataSource());
            ChargesEnquiryDataDbResponse db_response = db.getMClientData(inputChargesRequest.getRequestBody().getTransactionCode(), inputChargesRequest.getRequestBody().getProductCode(), inputChargesRequest.getRequestBody().getBranchCode(), inputChargesRequest.getRequestBody().getTransactionAmount(), response);
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
                        mapKafkaPayload.put("RequestType", "GET_CHARGES");
                        mapKafkaPayload.put("Amount", inputChargesRequest.getRequestBody().getTransactionAmount() + "");
                        mapKafkaPayload.put("ResponseCode", Integer.toString(response.getResponseCode()));
                        mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(response.getResponseMessage()) ? "" : response.getResponseMessage()));
                    } else {
                        mapKafkaPayload.put("SessionId", inputChargesRequest.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "GET_CHARGES");
                        mapKafkaPayload.put("Amount", inputChargesRequest.getRequestBody().getTransactionAmount() + "");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                    }
                } else {
                    mapKafkaPayload.put("SessionId", inputChargesRequest.getMessageId());
                    mapKafkaPayload.put("ReferenceNumber", "");
                    mapKafkaPayload.put("MobileNumber", "");
                    mapKafkaPayload.put("RequestType", "GET_CHARGES");
                    mapKafkaPayload.put("Amount", inputChargesRequest.getRequestBody().getTransactionAmount() + "");
                    mapKafkaPayload.put("ResponseCode", Integer.toString(response.getResponseCode()));
                    mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(response.getResponseMessage()) ? "" : response.getResponseMessage()));
                }
            } else {
                mapKafkaPayload.put("SessionId", inputChargesRequest.getMessageId());
                mapKafkaPayload.put("ReferenceNumber", "");
                mapKafkaPayload.put("MobileNumber", "");
                mapKafkaPayload.put("RequestType", "GET_CHARGES");
                mapKafkaPayload.put("Amount", inputChargesRequest.getRequestBody().getTransactionAmount() + "");
                mapKafkaPayload.put("ResponseCode", "1");
                mapKafkaPayload.put("ResponseDescription", "Failed");
            }

            Thread kafkaLog = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(0);
                        kafkaProducer.sendMessage("CustomersLibrary", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                    } catch (InterruptedException v) {
                        logger.error(ChargesEnquiryService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                    }
                }
            };
            kafkaLog.start();
        } catch (Exception ex) {
            logger.error(ChargesEnquiryService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            response.setResponseCode(1);
            response.setResponseMessage("Internal Server error.");
            retObj = Obj.writeValueAsString(response);
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
