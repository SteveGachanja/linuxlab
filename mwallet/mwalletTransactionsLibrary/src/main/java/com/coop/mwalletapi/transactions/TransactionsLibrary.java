package com.coop.mwalletapi.transactions;

import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import com.coop.mwalletapi.functions.Functions;
import com.coop.mwalletapi.kafka.KafkaLogProducer;
import com.coop.mwalletapi.transactions.dao.PostTransactionsDb;
import com.coop.mwalletapi.transactions.dao.QueryTransactionDb;
import com.coop.mwalletapi.transactions.dao.model.TransactionQueryDbResp;
import com.coop.mwalletapi.transactions.dao.model.TransactionQueryRequest;
import com.coop.mwalletapi.transactions.dao.model.TransactionQueryResponse;
import com.coop.mwalletapi.transactions.dao.model.TransactionsPostDbResp;
import com.coop.mwalletapi.transactions.entities.TransactionPostRequest;
import com.coop.mwalletapi.transactions.entities.resp.ResponseBody;
import com.coop.mwalletapi.transactions.entities.resp.TransactionsPostResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @author okahia
 */

public class TransactionsLibrary {
    private static final Logger logger = LogManager.getLogger(TransactionsLibrary.class.getName());

    public Object postTransactions(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        TransactionsPostResponse postResp = new TransactionsPostResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        KafkaLogProducer kafkaProducer = new KafkaLogProducer(commonDataReq.getKafkaConfigs().getServer(), commonDataReq.getKafkaConfigs().getClientId(), commonDataReq.getKafkaConfigs().getTopic());

        TransactionPostRequest suppliedPostData = new TransactionPostRequest();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        String jsonString = commonDataReq.getInObj().toString();
        logger.info(TransactionsLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + jsonString);
        suppliedPostData = mapper.readValue(jsonString, TransactionPostRequest.class);

        //Prepare the response body from the request
        ResponseBody respBody = new ResponseBody();
        respBody.setOperationParameters(suppliedPostData.getRequestBody().getOperationParameters());
        respBody.setTransactionItems(suppliedPostData.getRequestBody().getTransactionItems());

        try {

            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedPostData.getApiUser(), suppliedPostData.getApiPassword());
            if (validApiUser <= 0){
                postResp.setResponseCode(-1);
                postResp.setResponseMessage("API User not allowed");
                postResp.setMessageId(suppliedPostData.getMessageId());
                postResp.setResponseBody(respBody);
                retObj = Obj.writeValueAsString(postResp);
                return retObj;
            }

            //validations if any
            if (!func.validateDateTime(suppliedPostData.getRequestDateTime())){
                postResp.setResponseCode(-1);
                postResp.setResponseMessage("Check the Request Date");
                postResp.setMessageId(suppliedPostData.getMessageId());
                postResp.setResponseBody(respBody);
                retObj = Obj.writeValueAsString(postResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedPostData.getRequestDateTime())){
                    postResp.setResponseCode(-1);
                    postResp.setResponseMessage("Check the Request Date Must be today");
                    postResp.setMessageId(suppliedPostData.getMessageId());
                    postResp.setResponseBody(respBody);
                    retObj = Obj.writeValueAsString(postResp);
                    return retObj;
                }
            }

            if (!func.validateDateTime(suppliedPostData.getRequestBody().getOperationParameters().getMakerDateTime())){
                postResp.setResponseCode(1);
                postResp.setResponseMessage("Check the Maker Date");
                postResp.setMessageId(suppliedPostData.getMessageId());
                postResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(postResp);
                return retObj;
            }

            if (!func.validateDateTime(suppliedPostData.getRequestBody().getOperationParameters().getApproverDateTime())){
                postResp.setResponseCode(1);
                postResp.setResponseMessage("Check the Approver Date");
                postResp.setMessageId(suppliedPostData.getMessageId());
                postResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(postResp);
                return retObj;
            }

            if (!func.validateDateTime(suppliedPostData.getRequestBody().getOperationParameters().getValueDate())){
                postResp.setResponseCode(1);
                postResp.setResponseMessage("Check the Value Date");
                postResp.setMessageId(suppliedPostData.getMessageId());
                postResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(postResp);
                return retObj;
            }

            //validate message type
            switch (suppliedPostData.getRequestBody().getOperationParameters().getMessageType().toUpperCase()) {
                case "NORMAL":

                    break;
                case "REVERSAL":
                    if (func.isNullOrEmpty(suppliedPostData.getRequestBody().getOperationParameters().getTransactionID())){
                        postResp.setResponseCode(-1);
                        postResp.setResponseMessage("Check transaction Id its required when doing reversals");
                        postResp.setMessageId(suppliedPostData.getMessageId());
                        postResp.setResponseBody(respBody);
                        retObj = Obj.writeValueAsString(postResp);
                        return retObj;
                    }
                    break;
                default:

                    break;
            }

            // do the postings here
            HashMap<String, String> mapKafkaPayload = new HashMap<>();
            final String messageId = suppliedPostData.getMessageId();

            switch (suppliedPostData.getRequestBody().getOperationParameters().getMessageType().toUpperCase()) {
                case "NORMAL":
                    TransactionsPostDbResp transactionsPostDbResp = new TransactionsPostDbResp();
                    PostTransactionsDb postDb = new PostTransactionsDb(commonDataReq.getMwalletDataSource());

                    //do the normal postings here
                    transactionsPostDbResp = postDb.postTransactions(
                            validApiUser,
                            func.randomStrGen(),
                            suppliedPostData
                    );

                    postResp.setResponseCode(transactionsPostDbResp.getResponseCode());
                    postResp.setResponseMessage(transactionsPostDbResp.getResponseMessage());
                    postResp.setMessageId(transactionsPostDbResp.getMessageId());
                    respBody.getOperationParameters().setTransactionID(transactionsPostDbResp.getTransactionID());
                    postResp.setResponseBody(respBody);
                    retObj = Obj.writeValueAsString(postResp);

                    if (!func.isNullOrEmpty(Integer.toString(transactionsPostDbResp.getResponseCode()))) {
                        if (transactionsPostDbResp.getResponseCode() == 0) {
                            if (!func.isNullOrEmpty(transactionsPostDbResp.getResponseMessage())){
                                mapKafkaPayload.put("SessionId", suppliedPostData.getMessageId());
                                mapKafkaPayload.put("ReferenceNumber", suppliedPostData.getMessageId());
                                mapKafkaPayload.put("MobileNumber", "");
                                mapKafkaPayload.put("RequestType", "POST_TRANSACTIONS");
                                mapKafkaPayload.put("Amount", "0");
                                mapKafkaPayload.put("ResponseCode", Integer.toString(transactionsPostDbResp.getResponseCode()));
                                mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(transactionsPostDbResp.getResponseMessage()) ? "" : transactionsPostDbResp.getResponseMessage()));
                                mapKafkaPayload.put("RequestPayload", jsonString);
                                mapKafkaPayload.put("ChannelId", suppliedPostData.getChannelId());
                            } else {
                                mapKafkaPayload.put("SessionId", suppliedPostData.getMessageId());
                                mapKafkaPayload.put("ReferenceNumber", "");
                                mapKafkaPayload.put("MobileNumber", "");
                                mapKafkaPayload.put("RequestType", "POST_TRANSACTIONS");
                                mapKafkaPayload.put("Amount", "0");
                                mapKafkaPayload.put("ResponseCode", "1");
                                mapKafkaPayload.put("ResponseDescription", "Failed");
                                mapKafkaPayload.put("RequestPayload", jsonString);
                                mapKafkaPayload.put("ChannelId", suppliedPostData.getChannelId());
                            }
                        } else {
                            mapKafkaPayload.put("SessionId", suppliedPostData.getMessageId());
                            mapKafkaPayload.put("ReferenceNumber", "");
                            mapKafkaPayload.put("MobileNumber", "");
                            mapKafkaPayload.put("RequestType", "POST_TRANSACTIONS");
                            mapKafkaPayload.put("Amount", "0");
                            mapKafkaPayload.put("ResponseCode", Integer.toString(transactionsPostDbResp.getResponseCode()));
                            mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(transactionsPostDbResp.getResponseMessage()) ? "" : transactionsPostDbResp.getResponseMessage()));
                            mapKafkaPayload.put("RequestPayload", jsonString);
                            mapKafkaPayload.put("ChannelId", suppliedPostData.getChannelId());
                        }
                    } else {
                        mapKafkaPayload.put("SessionId", suppliedPostData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "POST_TRANSACTIONS");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                        mapKafkaPayload.put("RequestPayload", jsonString);
                        mapKafkaPayload.put("ChannelId", suppliedPostData.getChannelId());
                    }

                    Thread kafkaLogPostTrans = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(0);
                                kafkaProducer.sendMessage("CustomersLibrary", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                            } catch (InterruptedException v) {
                                logger.error(TransactionsLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                            }
                        }
                    };
                    kafkaLogPostTrans.start();

                    break;
                case "REVERSAL":
                    //do reversals here
                    TransactionsPostDbResp transactionsReverseDbResp = new TransactionsPostDbResp();
                    PostTransactionsDb reverseDb = new PostTransactionsDb(commonDataReq.getMwalletDataSource());

                    transactionsReverseDbResp = reverseDb.reverseTransactions(
                            validApiUser,
                            func.randomStrGen(),
                            suppliedPostData
                    );

                    postResp.setResponseCode(transactionsReverseDbResp.getResponseCode());
                    postResp.setResponseMessage(transactionsReverseDbResp.getResponseMessage());
                    postResp.setMessageId(transactionsReverseDbResp.getMessageId());
                    respBody.getOperationParameters().setTransactionID(transactionsReverseDbResp.getTransactionID());
                    postResp.setResponseBody(respBody);
                    retObj = Obj.writeValueAsString(postResp);


                    if (!func.isNullOrEmpty(Integer.toString(transactionsReverseDbResp.getResponseCode()))) {
                        if (transactionsReverseDbResp.getResponseCode() == 0) {
                            if (!func.isNullOrEmpty(transactionsReverseDbResp.getResponseMessage())){
                                mapKafkaPayload.put("SessionId", suppliedPostData.getMessageId());
                                mapKafkaPayload.put("ReferenceNumber", suppliedPostData.getMessageId());
                                mapKafkaPayload.put("MobileNumber", "");
                                mapKafkaPayload.put("RequestType", "REVERSE_TRANSACTIONS");
                                mapKafkaPayload.put("Amount", "0");
                                mapKafkaPayload.put("ResponseCode", Integer.toString(transactionsReverseDbResp.getResponseCode()));
                                mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(transactionsReverseDbResp.getResponseMessage()) ? "" : transactionsReverseDbResp.getResponseMessage()));
                                mapKafkaPayload.put("RequestPayload", jsonString);
                                mapKafkaPayload.put("ChannelId", suppliedPostData.getChannelId());
                            } else {
                                mapKafkaPayload.put("SessionId", suppliedPostData.getMessageId());
                                mapKafkaPayload.put("ReferenceNumber", "");
                                mapKafkaPayload.put("MobileNumber", "");
                                mapKafkaPayload.put("RequestType", "REVERSE_TRANSACTIONS");
                                mapKafkaPayload.put("Amount", "0");
                                mapKafkaPayload.put("ResponseCode", "1");
                                mapKafkaPayload.put("ResponseDescription", "Failed");
                                mapKafkaPayload.put("RequestPayload", jsonString);
                                mapKafkaPayload.put("ChannelId", suppliedPostData.getChannelId());
                            }
                        } else {
                            mapKafkaPayload.put("SessionId", suppliedPostData.getMessageId());
                            mapKafkaPayload.put("ReferenceNumber", "");
                            mapKafkaPayload.put("MobileNumber", "");
                            mapKafkaPayload.put("RequestType", "REVERSE_TRANSACTIONS");
                            mapKafkaPayload.put("Amount", "0");
                            mapKafkaPayload.put("ResponseCode", Integer.toString(transactionsReverseDbResp.getResponseCode()));
                            mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(transactionsReverseDbResp.getResponseMessage()) ? "" : transactionsReverseDbResp.getResponseMessage()));
                            mapKafkaPayload.put("RequestPayload", jsonString);
                            mapKafkaPayload.put("ChannelId", suppliedPostData.getChannelId());
                        }
                    } else {
                        mapKafkaPayload.put("SessionId", suppliedPostData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "REVERSE_TRANSACTIONS");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                        mapKafkaPayload.put("RequestPayload", jsonString);
                        mapKafkaPayload.put("ChannelId", suppliedPostData.getChannelId());
                    }

                    Thread kafkaLogReverseTrans = new Thread() {
                        public void run() {
                            try {
                                Thread.sleep(0);
                                kafkaProducer.sendMessage("CustomersLibrary", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                            } catch (InterruptedException v) {
                                logger.error(TransactionsLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                            }
                        }
                    };
                    kafkaLogReverseTrans.start();

                    break;
            }


        } catch (Exception ex) {
            logger.error(TransactionsLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            postResp.setResponseCode(-1);
            postResp.setResponseMessage("Error");
            postResp.setMessageId(suppliedPostData.getMessageId());
            postResp.setResponseBody(respBody);
            retObj = Obj.writeValueAsString(postResp);
        }
        return retObj;
    }

    public Object queryTransaction(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        TransactionQueryResponse queryResp = new TransactionQueryResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();

        TransactionQueryRequest suppliedQueryData = new TransactionQueryRequest();
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        String jsonString = commonDataReq.getInObj().toString();
        logger.info(TransactionsLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + jsonString);
        suppliedQueryData = mapper.readValue(jsonString, TransactionQueryRequest.class);

        //Prepare the response body from the request
        TransactionQueryDbResp respBody = new TransactionQueryDbResp();

        try {
            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedQueryData.getApiUser(), suppliedQueryData.getApiPassword());
            if (validApiUser <= 0){
                queryResp.setResponseCode(-1);
                queryResp.setResponseMessage("API User not allowed");
                queryResp.setMessageId(suppliedQueryData.getMessageId());
                queryResp.setResponseBody(respBody);
                retObj = Obj.writeValueAsString(queryResp);
                return retObj;
            }

            //validations if any
            if (!func.validateDateTime(suppliedQueryData.getRequestDateTime())){
                queryResp.setResponseCode(-1);
                queryResp.setResponseMessage("Check the Request Date");
                queryResp.setMessageId(suppliedQueryData.getMessageId());
                queryResp.setResponseBody(respBody);
                retObj = Obj.writeValueAsString(queryResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedQueryData.getRequestDateTime())){
                    queryResp.setResponseCode(-1);
                    queryResp.setResponseMessage("Check the Request Date Must be today");
                    queryResp.setMessageId(suppliedQueryData.getMessageId());
                    queryResp.setResponseBody(respBody);
                    retObj = Obj.writeValueAsString(queryResp);
                    return retObj;
                }
            }

            /*if (!func.validateDateTime(suppliedQueryData.getRequestBody().getMakerDateTime())){
                queryResp.setResponseCode(1);
                queryResp.setResponseMessage("Check the Maker Date");
                queryResp.setMessageId(suppliedQueryData.getMessageId());
                queryResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(queryResp);
                return retObj;
            }*/

            /*if (!func.validateDateTime(suppliedQueryData.getRequestBody().getApproverDateTime())){
                queryResp.setResponseCode(1);
                queryResp.setResponseMessage("Check the Approver Date");
                queryResp.setMessageId(suppliedQueryData.getMessageId());
                queryResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(queryResp);
                return retObj;
            }*/

            TransactionQueryResponse transactionQueryResp = new TransactionQueryResponse();
            QueryTransactionDb queryTransactionDb = new QueryTransactionDb(commonDataReq.getMwalletDataSource());

            //Execute the Query/SP
            transactionQueryResp = queryTransactionDb.queryTransaction(func.randomStrGen(), suppliedQueryData, queryResp);
            if(transactionQueryResp.getResponseCode() == 0) {
                queryResp = transactionQueryResp; //Set to the returned Values
                queryResp.setMessageId(suppliedQueryData.getMessageId());
            } else {
                logger.error("Error: " + transactionQueryResp.getResponseMessage());
                queryResp.setResponseCode(transactionQueryResp.getResponseCode());
                queryResp.setResponseMessage(transactionQueryResp.getResponseMessage());
                queryResp.setMessageId(suppliedQueryData.getMessageId());
                queryResp.setResponseBody(null);
            }
            retObj = Obj.writeValueAsString(queryResp);

        } catch (Exception ex) {
            logger.error(TransactionsLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
            queryResp.setResponseCode(-1);
            queryResp.setResponseMessage("Error");
            queryResp.setMessageId(suppliedQueryData.getMessageId());
            queryResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(queryResp);
        }
        return retObj;
    }
}
