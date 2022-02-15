package com.coop.mwalletapi.mwalletupdateaccountflaglibrary.services;

import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import com.coop.mwalletapi.functions.Functions;
import com.coop.mwalletapi.mwalletupdateaccountflaglibrary.dao.Db;
import com.coop.mwalletapi.mwalletupdateaccountflaglibrary.dao.model.UpdateAccountFlagRespData;
import com.coop.mwalletapi.mwalletupdateaccountflaglibrary.entities.UpdateAccountFlagDbRequest;
import com.coop.mwalletapi.mwalletupdateaccountflaglibrary.entities.UpdateAccountFlagDbResponse;
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
public class UpdateAccountFlagService {

    private static final Logger logger = LogManager.getLogger(UpdateAccountFlagService.class.getName());

    public Object updateAccountFlag(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        UpdateAccountFlagDbResponse response = new UpdateAccountFlagDbResponse();

        response.setResponseCode(1);
        response.setResponseMessage("Processing");

        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        try {
            UpdateAccountFlagDbRequest inputRequest = new UpdateAccountFlagDbRequest();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myRequestJson = commonDataReq.getInObj().toString();
            logger.info(UpdateAccountFlagService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myRequestJson);
            inputRequest = mapper.readValue(myRequestJson, UpdateAccountFlagDbRequest.class);

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

            if (func.isNullOrEmpty(inputRequest.getRequestBody().getReason())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the Reason for Flagging");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }

            //ChannelID is Supplied in the request Header
            /*if (func.isNullOrEmpty(inputRequest.getRequestBody().getChannelID())) {
                response.setResponseCode(1);
                response.setResponseMessage("Check the ChannelID Value");
                retObj = Obj.writeValueAsString(response);
                return retObj;
            }*/

            //Transform the Date to Java Standard Format
            /*String iActionDate = inputRequest.getRequestBody().getActionDate().toString();
            if (iActionDate.contains(" ")){
                iActionDate = iActionDate.replace(" ", "T");
            }
            if (!validateDateTime(iActionDate)){
                response.setResponseCode(1);
                response.setResponseMessage("Check the Action Date");
                response.setMessageId(inputRequest.getMessageId());
                response.setResponseBody(null);
                retObj = Obj.writeValueAsString(response);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), iActionDate)){
                    response.setResponseCode(1);
                    response.setResponseMessage("Check the ActionDate Must be today");
                    response.setMessageId(inputRequest.getMessageId());
                    response.setResponseBody(null);
                    retObj = Obj.writeValueAsString(response);
                    return retObj;
                }
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
            UpdateAccountFlagRespData db_response = db.getUpdateAccountFlagDbResponse(inputRequest.getRequestBody().getEntityID(),
                    inputRequest.getRequestBody().getActionType(),
                    inputRequest.getRequestBody().getAccountNumber(),
                    inputRequest.getRequestBody().getReason(),
                    inputRequest.getChannelId(), //inputRequest.getRequestBody().getChannelID(),
                    //jActionDate, //inputRequest.getRequestBody().getActionDate(),
                    inputRequest.getRequestBody().getStatus(),
                    inputRequest.getRequestBody().getMakerID(),
                    jMakerDate, //inputRequest.getRequestBody().getMakerDate(),
                    inputRequest.getRequestBody().getApproverID(),
                    jApproverDate, //inputRequest.getRequestBody().getApproverDate(),
                    response);

            response.setResponseBody(db_response);

            retObj = Obj.writeValueAsString(response);

        } catch (Exception ex) {
            logger.error(UpdateAccountFlagService.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

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