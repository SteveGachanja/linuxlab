package com.coop.mwalletapi.mwalletblockamountlibrary.services;

import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import com.coop.mwalletapi.functions.Functions;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.Db;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.BlockAmountRespData;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.BlockedAmountQueryRespData;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.UnblockAmountRespData;
import com.coop.mwalletapi.mwalletblockamountlibrary.entities.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author fosano
 */
public class BlockUnblockAmountService {

    private static final Logger logger = LogManager.getLogger(BlockUnblockAmountService.class.getName());

    public Object blockAmount(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        BlockAmountDbResponse response = new BlockAmountDbResponse();

        response.setResponseCode(1);
        response.setResponseMessage("Processing");

        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        try {
            BlockAmountDbRequest inputRequest = new BlockAmountDbRequest();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myRequestJson = commonDataReq.getInObj().toString();
            logger.info(BlockUnblockAmountService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myRequestJson);
            inputRequest = mapper.readValue(myRequestJson, BlockAmountDbRequest.class);

            response.setMessageId(inputRequest.getMessageId());
            //validate credentials
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), inputRequest.getApiUser(), inputRequest.getApiPassword());
            if (validApiUser <= 0) {
                response.setResponseCode(1);
                response.setResponseMessage("API user not authorised.");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            //Transform the Date to Java Standard Format
            String jDate = inputRequest.getRequestDateTime().toString();
            if (jDate.contains(" ")){
                jDate = jDate.replace(" ", "T");
            }

            if (!validateDateTime(jDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Request Date");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), inputRequest.getRequestDateTime())){
                    response.setResponseCode(1);
                    response.setResponseMessage("Check the Request Date Must be today");
                    response.setMessageId(inputRequest.getMessageId());
                    response.setResponseBody(null);
                    retObj = Obj.writeValueAsString(response);
                    return retObj;
                }
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getEntityID())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Entity Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getActionType())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the ActionType Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getAccountNumber())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Account Number Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getBlockAmount().toString())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Amount to Block");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getReason())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Reason for Flagging");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            //Channel ID
            /*String iBlockDate = inputRequest.getRequestBody().getBlockDate().toString();
            if (iBlockDate.contains(" ")){
                iBlockDate = iBlockDate.replace(" ", "T");
            }
            if (!validateDateTime(iBlockDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Date Account is to be Blocked");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }*/

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getExternalReference())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the External Reference Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            /*if (func.isNullOrEmpty(inputRequest.getRequestBody().getActive())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Active Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }*/

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getStatus())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Status Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getMakerID())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the MakerID Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            String iMakerDate = inputRequest.getRequestBody().getMakerDate().toString();
            if (iMakerDate.contains(" ")){
                iMakerDate = iMakerDate.replace(" ", "T");
            }
            if (!validateDateTime(iMakerDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Maker Date");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getApproverID())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Approver Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            String iApproverDate = inputRequest.getRequestBody().getApproverDate().toString();
            if (iApproverDate.contains(" ")){
                iApproverDate = iApproverDate.replace(" ", "T");
            }
            if (!validateDateTime(iApproverDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Approver Date");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            /*LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String text = date.format(formatter);
            LocalDate parsedDate = LocalDate.parse(text, formatter);*/

            //Date jActionDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(iActionDate);
            Date jMakerDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(iMakerDate);
            Date jApproverDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(iApproverDate);

            Db db = new Db(commonDataReq.getMwalletDataSource());
            BlockAmountRespData db_response = db.postBlockAmountDbResponse(inputRequest.getRequestBody().getEntityID(),
                    inputRequest.getRequestBody().getActionType(),
                    inputRequest.getRequestBody().getAccountNumber(),
                    inputRequest.getRequestBody().getBlockAmount(),
                    inputRequest.getRequestBody().getReason(),
                    inputRequest.getChannelId(), //inputRequest.getRequestBody().getChannelID(),
                    //inputRequest.getRequestBody().getBlockDate(),
                    inputRequest.getRequestBody().getExternalReference(),
                    //inputRequest.getRequestBody().getActive(),
                    inputRequest.getRequestBody().getStatus(),
                    inputRequest.getRequestBody().getMakerID(),
                    jMakerDate, //inputRequest.getRequestBody().getMakerDate(),
                    inputRequest.getRequestBody().getApproverID(),
                    jApproverDate, //inputRequest.getRequestBody().getApproverDate(),
                    response);

            response.setResponseBody(db_response);

            retObj = Obj.writeValueAsString(response);

        } catch (Exception ex) {
            logger.error(BlockUnblockAmountService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            response.setResponseCode(1);
            response.setResponseMessage("Internal Server error.");
            retObj = Obj.writeValueAsString(response);
        }
        return retObj;
    }

    public Object unblockAmount(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        UnblockAmountDbResponse response = new UnblockAmountDbResponse();

        response.setResponseCode(1);
        response.setResponseMessage("Processing");

        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        try {
            UnblockAmountDbRequest inputRequest = new UnblockAmountDbRequest();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myRequestJson = commonDataReq.getInObj().toString();
            logger.info(BlockUnblockAmountService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myRequestJson);
            inputRequest = mapper.readValue(myRequestJson, UnblockAmountDbRequest.class);

            response.setMessageId(inputRequest.getMessageId());
            //validate credentials
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), inputRequest.getApiUser(), inputRequest.getApiPassword());
            if (validApiUser <= 0) {
                response.setResponseCode(1);
                response.setResponseMessage("API user not authorised.");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            //Transform the Date to Java Standard Format
            String jDate = inputRequest.getRequestDateTime().toString();
            if (jDate.contains(" ")) {
                jDate = jDate.replace(" ", "T");
            }

            if (!validateDateTime(jDate)) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Request Date");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), inputRequest.getRequestDateTime())) {
                    response.setResponseCode(1);
                    response.setResponseMessage("Check the Request Date Must be today");
                    response.setMessageId(inputRequest.getMessageId());
                    response.setResponseBody(null);
                    retObj = Obj.writeValueAsString(response);
                    return retObj;
                }
            }

            if (inputRequest.getRequestBody().getRequestID() == null) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the RequestID Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            } else {
                if (func.isNullOrEmpty(inputRequest.getRequestBody().getRequestID().toString())) {
                    response.setResponseCode(1);
                    response.setResponseMessage("Check the RequestID Value");
                    retObj = Obj.writeValueAsString(response);
                    return retObj;
                }
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getEntityID())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Entity Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getActionType())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the ActionType Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getAccountNumber())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Account Number Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getBlockAmount().toString())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Amount to Block");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getReason())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Reason for Flagging");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            //Channel ID
            /*String iBlockDate = inputRequest.getRequestBody().getBlockDate().toString();
            if (iBlockDate.contains(" ")){
                iBlockDate = iBlockDate.replace(" ", "T");
            }
            if (!validateDateTime(iBlockDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Date Account is to be Blocked");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }*/

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getExternalReference())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the External Reference Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            /*if (func.isNullOrEmpty(inputRequest.getRequestBody().getActive())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Active Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }*/

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getStatus())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Status Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            String iMakerDate = inputRequest.getRequestBody().getMakerDate().toString();
            if (iMakerDate.contains(" ")){
                iMakerDate = iMakerDate.replace(" ", "T");
            }
            if (!validateDateTime(iMakerDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Maker Date");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getApproverID())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Approver Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            String iApproverDate = inputRequest.getRequestBody().getApproverDate().toString();
            if (iApproverDate.contains(" ")){
                iApproverDate = iApproverDate.replace(" ", "T");
            }
            if (!validateDateTime(iApproverDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Approver Date");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            /*LocalDate date = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String text = date.format(formatter);
            LocalDate parsedDate = LocalDate.parse(text, formatter);*/

            //Date jActionDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(iActionDate);
            Date jMakerDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(iMakerDate);
            Date jApproverDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(iApproverDate);

            Db db = new Db(commonDataReq.getMwalletDataSource());
            UnblockAmountRespData db_response = db.postUnblockAmountDbResponse(inputRequest.getRequestBody().getRequestID(),
                    inputRequest.getRequestBody().getEntityID(),
                    inputRequest.getRequestBody().getActionType(),
                    inputRequest.getRequestBody().getAccountNumber(),
                    inputRequest.getRequestBody().getBlockAmount(),
                    inputRequest.getRequestBody().getReason(),
                    inputRequest.getChannelId(), //inputRequest.getRequestBody().getChannelID(),
                    //inputRequest.getRequestBody().getBlockDate(),
                    inputRequest.getRequestBody().getExternalReference(),
                    //inputRequest.getRequestBody().getActive(),
                    inputRequest.getRequestBody().getStatus(),
                    inputRequest.getRequestBody().getMakerID(),
                    jMakerDate, //inputRequest.getRequestBody().getMakerDate(),
                    inputRequest.getRequestBody().getApproverID(),
                    jApproverDate, //inputRequest.getRequestBody().getApproverDate(),
                    response);

            response.setResponseBody(db_response);

            retObj = Obj.writeValueAsString(response);

        } catch (Exception ex) {
            logger.error(BlockUnblockAmountService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            response.setResponseCode(1);
            response.setResponseMessage("Internal Server error.");
            retObj = Obj.writeValueAsString(response);
        }
        return retObj;
    }

    public Object blockedAmountQuery(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        BlockedAmountQueryDbResponse response = new BlockedAmountQueryDbResponse();

        response.setResponseCode(1);
        response.setResponseMessage("Processing");

        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        try {
            BlockedAmountQueryDbRequest inputRequest = new BlockedAmountQueryDbRequest();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myRequestJson = commonDataReq.getInObj().toString();
            logger.info(BlockUnblockAmountService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myRequestJson);
            inputRequest = mapper.readValue(myRequestJson, BlockedAmountQueryDbRequest.class);

            response.setMessageId(inputRequest.getMessageId());
            //validate credentials
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), inputRequest.getApiUser(), inputRequest.getApiPassword());
            if (validApiUser <= 0) {
                response.setResponseCode(1);
                response.setResponseMessage("API user not authorised.");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            //Transform the Date to Java Standard Format
            String jDate = inputRequest.getRequestDateTime().toString();
            if (jDate.contains(" ")){
                jDate = jDate.replace(" ", "T");
            }

            if (!validateDateTime(jDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Request Date");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), inputRequest.getRequestDateTime())){
                    response.setResponseCode(1);
                    response.setResponseMessage("Check the Request Date Must be today");
                    response.setMessageId(inputRequest.getMessageId());
                    response.setResponseBody(null);
                    retObj = Obj.writeValueAsString(response);
                    return retObj;
                }
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getActionType())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the ActionType Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getActive().toString())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Active Parameter");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getAccountNumber())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Account Number Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            Db db = new Db(commonDataReq.getMwalletDataSource());
            BlockedAmountQueryRespData db_response = db.getBlockedAmountQueryDbResponse(inputRequest.getRequestBody().getActionType(),
                    inputRequest.getRequestBody().getActive(),
                    inputRequest.getRequestBody().getAccountNumber(),
                    response);

            response.setResponseBody(db_response);

            retObj = Obj.writeValueAsString(response);

        } catch (Exception ex) {
            logger.error(BlockUnblockAmountService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

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