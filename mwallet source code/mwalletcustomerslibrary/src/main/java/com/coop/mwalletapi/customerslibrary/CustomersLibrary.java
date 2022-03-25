package com.coop.mwalletapi.customerslibrary;

import com.coop.mwalletapi.functions.Functions;
import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import com.coop.mwalletapi.customerslibrary.dao.Db;
import com.coop.mwalletapi.customerslibrary.dao.model.CheckEmailDbResp;
import com.coop.mwalletapi.customerslibrary.dao.model.CreateAdditionalCustomerWalletDbResp;
import com.coop.mwalletapi.customerslibrary.dao.model.CreateCustomerDbResp;
import com.coop.mwalletapi.customerslibrary.dao.model.EditCustomerDbResp;
import com.coop.mwalletapi.customerslibrary.dao.model.GetCustomerDataDb;
import com.coop.mwalletapi.customerslibrary.dao.model.GetCustomerDataDbResp;
import com.coop.mwalletapi.customerslibrary.entities.checkemail.CheckEmailResponse;
import com.coop.mwalletapi.customerslibrary.entities.checkemail.CheckEmailResponseData;
import com.coop.mwalletapi.customerslibrary.entities.checkemail.SuppliedCheckEmailData;
import com.coop.mwalletapi.customerslibrary.entities.customeradditionalwallet.SuppliedCustomerCreateAdditionalWalletData;
import com.coop.mwalletapi.customerslibrary.entities.customeradditionalwallet.response.CreateAdditionalCustomerWalletResponse;
import com.coop.mwalletapi.customerslibrary.entities.customeradditionalwallet.response.CreateAdditionalCustomerWalletResponseData;
import com.coop.mwalletapi.customerslibrary.entities.customercreate.SuppliedCustomerCreateData;
import com.coop.mwalletapi.customerslibrary.entities.customercreate.response.CreateCustomerResponse;
import com.coop.mwalletapi.customerslibrary.entities.customercreate.response.CreateCustomerResponseData;
import com.coop.mwalletapi.customerslibrary.entities.customeredit.SuppliedCustomerEditData;
import com.coop.mwalletapi.customerslibrary.entities.customeredit.response.EditCustomerResponse;
import com.coop.mwalletapi.customerslibrary.entities.customeredit.response.EditCustomerResponseData;
import com.coop.mwalletapi.customerslibrary.entities.customerget.SuppliedCustomerGetDetailsData;
import com.coop.mwalletapi.customerslibrary.entities.customerget.customerresponse.GetCustomerResponse;
import com.coop.mwalletapi.customerslibrary.entities.customerget.customerresponse.GetCustomerResponseData;
import com.coop.mwalletapi.kafka.KafkaLogProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author okahia
 */
public class CustomersLibrary {

    private static final Logger logger = LogManager.getLogger(CustomersLibrary.class.getName());

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    
    public Object getCustomerDetails(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        GetCustomerResponse custResp = new GetCustomerResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        SuppliedCustomerGetDetailsData suppliedCustData = new SuppliedCustomerGetDetailsData();
        Functions func = new Functions();
        KafkaLogProducer kafkaProducer = new KafkaLogProducer(commonDataReq.getKafkaConfigs().getServer(), commonDataReq.getKafkaConfigs().getClientId(), commonDataReq.getKafkaConfigs().getTopic());
        
        try {
            GetCustomerDataDbResp customerDataDbResp = new GetCustomerDataDbResp();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedCustData = mapper.readValue(myCustomerJson, SuppliedCustomerGetDetailsData.class);
            
            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedCustData.getApiUser(), suppliedCustData.getApiPassword());
            if (validApiUser <= 0){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("API User not allowed");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }

            //validations if any
            if (!func.validateDateTime(suppliedCustData.getRequestDateTime())){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Request Date");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedCustData.getRequestDateTime())){
                    custResp.setResponseCode(1);
                    custResp.setResponseMessage("Check the Request Date Must be today");
                    custResp.setMessageId(suppliedCustData.getMessageId());
                    custResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(custResp);
                    return retObj;
                }
            }
            
            String documentType = ""; String documentNo = ""; String mobileNo = ""; String customerNo = "";
            String documentTypeNo = "";
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getDocumentNo()) && 
                    (func.isNullOrEmpty(suppliedCustData.getRequestBody().getMobileNo())) &&
                    (func.isNullOrEmpty(suppliedCustData.getRequestBody().getCustomerNo()))
                    ) {
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Search Criteria");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getDocumentType())) {
                documentType = "";
            } else {
                documentType = suppliedCustData.getRequestBody().getDocumentType();
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getDocumentNo())) {
                documentNo = "";
            } else {
                documentNo = suppliedCustData.getRequestBody().getDocumentNo();
                documentTypeNo = documentNo;
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getMobileNo())) {
                mobileNo = "";
            } else {
                mobileNo = suppliedCustData.getRequestBody().getMobileNo();
                documentTypeNo = mobileNo;
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getCustomerNo())) {
                customerNo = "";
            } else {
                customerNo = suppliedCustData.getRequestBody().getCustomerNo();
                documentTypeNo = customerNo;
            }

            List<GetCustomerDataDb> customerDataDb = null;
            GetCustomerResponseData custData = new GetCustomerResponseData();
            
            Db db = new Db(commonDataReq.getMwalletDataSource());
            customerDataDbResp = db.getMClientData(
                    validApiUser,
                    documentType,
                    documentNo,
                    mobileNo,
                    customerNo
            );
            if (!customerDataDbResp.getMCustData().isEmpty()) {
                custData.setMCustData(customerDataDbResp.getMCustData());
                custResp.setResponseCode(customerDataDbResp.getResponseCode());
                custResp.setResponseMessage(customerDataDbResp.getResponseMessage());
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(custData);
            } else {
                custData.setMCustData(customerDataDb);
                custResp.setResponseCode(customerDataDbResp.getResponseCode());
                custResp.setResponseMessage(customerDataDbResp.getResponseMessage());
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(custData);
            }

            retObj = Obj.writeValueAsString(custResp);

            HashMap<String, String> mapKafkaPayload = new HashMap<>();
            final String messageId = suppliedCustData.getMessageId();
            if (!func.isNullOrEmpty(Integer.toString(customerDataDbResp.getResponseCode()))) {
                if (customerDataDbResp.getResponseCode() == 0) {
                    if (!func.isNullOrEmpty(customerDataDbResp.getResponseMessage())){
                        mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", documentTypeNo);
                        mapKafkaPayload.put("RequestType", "GET_CUSTOMER");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", Integer.toString(customerDataDbResp.getResponseCode()));
                        mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(customerDataDbResp.getResponseMessage()) ? "" : customerDataDbResp.getResponseMessage()));
                        mapKafkaPayload.put("RequestPayload", myCustomerJson);
                        mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                    } else {
                        mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", documentTypeNo);
                        mapKafkaPayload.put("RequestType", "GET_CUSTOMER");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                        mapKafkaPayload.put("RequestPayload", myCustomerJson);
                        mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                    }
                } else {
                    mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                    mapKafkaPayload.put("ReferenceNumber", "");
                    mapKafkaPayload.put("MobileNumber", documentTypeNo);
                    mapKafkaPayload.put("RequestType", "GET_CUSTOMER");
                    mapKafkaPayload.put("Amount", "0");
                    mapKafkaPayload.put("ResponseCode", Integer.toString(customerDataDbResp.getResponseCode()));
                    mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(customerDataDbResp.getResponseMessage()) ? "" : customerDataDbResp.getResponseMessage()));
                    mapKafkaPayload.put("RequestPayload", myCustomerJson);
                    mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                }
            } else {
                mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                mapKafkaPayload.put("ReferenceNumber", "");
                mapKafkaPayload.put("MobileNumber", documentTypeNo);
                mapKafkaPayload.put("RequestType", "GET_CUSTOMER");
                mapKafkaPayload.put("Amount", "0");
                mapKafkaPayload.put("ResponseCode", "1");
                mapKafkaPayload.put("ResponseDescription", "Failed");
                mapKafkaPayload.put("RequestPayload", myCustomerJson);
                mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
            }
                        
            Thread kafkaLog = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(0);
                        kafkaProducer.sendMessage("CustomersLibrary", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                    } catch (InterruptedException v) {
                        logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                    }
                }
            };
            kafkaLog.start();
            
        } catch (Exception ex) {
            logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            custResp.setResponseCode(1);
            custResp.setResponseMessage("Error");
            custResp.setMessageId(suppliedCustData.getMessageId());
            custResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(custResp);
        }
        return retObj;
    }

    public Object createCustomer(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        CreateCustomerResponse custResp = new CreateCustomerResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        SuppliedCustomerCreateData suppliedCustData = new SuppliedCustomerCreateData();
        Functions func = new Functions();
        KafkaLogProducer kafkaProducer = new KafkaLogProducer(commonDataReq.getKafkaConfigs().getServer(), commonDataReq.getKafkaConfigs().getClientId(), commonDataReq.getKafkaConfigs().getTopic());
        
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedCustData = mapper.readValue(myCustomerJson, SuppliedCustomerCreateData.class);

            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedCustData.getApiUser(), suppliedCustData.getApiPassword());
            if (validApiUser <= 0){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("API User not allowed");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }
            
            //validations if any
            if (!func.validateDateTime(suppliedCustData.getRequestDateTime())){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Request Date");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedCustData.getRequestDateTime())){
                    custResp.setResponseCode(1);
                    custResp.setResponseMessage("Check the Request Date Must be today");
                    custResp.setMessageId(suppliedCustData.getMessageId());
                    custResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(custResp);
                    return retObj;
                }
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getDocumentType())) {
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Document Type");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getDocumentNumber())) {
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Document Number");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }
            
            if (func.hasSpecialCharacters(suppliedCustData.getRequestBody().getDocumentNumber())){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Document Number");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getMobileNumber())) {
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Mobile Number");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getFirstName())) {
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the First Name");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            } else {
                if (suppliedCustData.getRequestBody().getFirstName().length() <= 2){
                    custResp.setResponseCode(1);
                    custResp.setResponseMessage("Check the First Name Length");
                    custResp.setMessageId(suppliedCustData.getMessageId());
                    custResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(custResp);
                    return retObj;
                }
            }
            
            //if email is supplied then validate it
            if (!func.isNullOrEmpty(suppliedCustData.getRequestBody().getEmailAddress())) {
                boolean isValidEmailAddress = func.emailValidate(suppliedCustData.getRequestBody().getEmailAddress());
                if (!isValidEmailAddress) {
                    custResp.setResponseCode(1);
                    custResp.setResponseMessage("Check the Email");
                    custResp.setMessageId(suppliedCustData.getMessageId());
                    custResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(custResp);
                    return retObj;
                }
            }
            
            
            String dob;
            if (!func.validateDate(suppliedCustData.getRequestBody().getDateOfBirth())){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Date of Birth");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            } else {
                if (!func.validateDob(new SimpleDateFormat("dd-MM-yyyy").format(new Date()), suppliedCustData.getRequestBody().getDateOfBirth())){
                    custResp.setResponseCode(1);
                    custResp.setResponseMessage("Check the Date of Birth");
                    custResp.setMessageId(suppliedCustData.getMessageId());
                    custResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(custResp);
                    return retObj;
                }
                dob = func.formatDob(suppliedCustData.getRequestBody().getDateOfBirth());
            }

            
//            String dateOfBirth = suppliedCustData.getRequestBody().getDateOfBirth();
//            try {
//                Date dobDate = new SimpleDateFormat("dd-MM-yyyy").parse(dateOfBirth);
//                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
//                dob = formatter.format(dobDate);
//            } catch (ParseException ex) {
//                java.util.logging.Logger.getLogger(CustomersLibrary.class.getName()).log(Level.SEVERE, null, ex);
//
//                custResp.setResponseCode(1);
//                custResp.setResponseMessage("Check the Date of Birth");
//                custResp.setMessageId(suppliedCustData.getMessageId());
//                custResp.setResponseBody(null);
//                retObj = Obj.writeValueAsString(custResp);
//                return retObj;
//            }
            
            String makerdate;
            if (!func.validateDateTime(suppliedCustData.getRequestBody().getMakerDateTime())){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Maker Date");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            } else {
//                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedCustData.getRequestBody().getMakerDateTime())){
//                    custResp.setResponseCode(1);
//                    custResp.setResponseMessage("Check the Maker Date Must be today");
//                    custResp.setMessageId(suppliedCustData.getMessageId());
//                    custResp.setResponseBody(null);
//                    retObj = Obj.writeValueAsString(custResp);
//                    return retObj;
//                }
                makerdate = func.formatDateTime(suppliedCustData.getRequestBody().getMakerDateTime());
            }
            
            String approverDate;
            if (!func.validateDateTime(suppliedCustData.getRequestBody().getApproverDateTime())){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Approver Date");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            } else {
//                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedCustData.getRequestBody().getApproverDateTime())){
//                    custResp.setResponseCode(1);
//                    custResp.setResponseMessage("Check the Approver Date Must be today");
//                    custResp.setMessageId(suppliedCustData.getMessageId());
//                    custResp.setResponseBody(null);
//                    retObj = Obj.writeValueAsString(custResp);
//                    return retObj;
//                }
                approverDate = func.formatDateTime(suppliedCustData.getRequestBody().getApproverDateTime());
            }

            CreateCustomerResponseData custData = new CreateCustomerResponseData();
            CreateCustomerDbResp createCustomerDbResp = new CreateCustomerDbResp();

            Db db = new Db(commonDataReq.getMwalletDataSource());
            createCustomerDbResp = db.createMClientData(
                    validApiUser,
                    suppliedCustData.getRequestBody().getProductCode(), suppliedCustData.getRequestBody().getDocumentType(),
                    suppliedCustData.getRequestBody().getDocumentNumber(), suppliedCustData.getRequestBody().getMobileNumber(),
                    suppliedCustData.getRequestBody().getEmailAddress(), suppliedCustData.getRequestBody().getTitle(),
                    suppliedCustData.getRequestBody().getFirstName(), suppliedCustData.getRequestBody().getMiddleName(),
                    suppliedCustData.getRequestBody().getLastName(), dob,
                    suppliedCustData.getRequestBody().getGender(), 
                    (func.isNullOrEmpty(suppliedCustData.getRequestBody().getNationality()) ? "0" : suppliedCustData.getRequestBody().getNationality()),
                    suppliedCustData.getRequestBody().getBranchCode(),
                    suppliedCustData.getRequestBody().getAccountOfficerCode(), suppliedCustData.getRequestBody().getSalesOfficerCode(),
                    suppliedCustData.getRequestBody().getSegmentId(), 1,
                    suppliedCustData.getRequestBody().getMakerUserId(), makerdate,
                    suppliedCustData.getRequestBody().getApproverUserId(), approverDate
            );

            custData.setCustomerNumber(createCustomerDbResp.getCustomerNumber());
            custData.setAccountNumber(createCustomerDbResp.getAccountNumber());
            custData.setAccountId(createCustomerDbResp.getAccountId());
            custResp.setResponseCode(createCustomerDbResp.getResponseCode());
            custResp.setResponseMessage(createCustomerDbResp.getResponseMessage());
            custResp.setMessageId(suppliedCustData.getMessageId());
            custResp.setResponseBody(custData);

            retObj = Obj.writeValueAsString(custResp);
            
            HashMap<String, String> mapKafkaPayload = new HashMap<>();
            final String messageId = suppliedCustData.getMessageId();
            if (!func.isNullOrEmpty(Integer.toString(createCustomerDbResp.getResponseCode()))) {
                if (createCustomerDbResp.getResponseCode() == 0) {
                    if (!func.isNullOrEmpty(createCustomerDbResp.getResponseMessage())){
                        mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", suppliedCustData.getMessageId());
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "CREATE_CUSTOMER");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", Integer.toString(createCustomerDbResp.getResponseCode()));
                        mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(createCustomerDbResp.getResponseMessage()) ? "" : createCustomerDbResp.getResponseMessage()));
                        mapKafkaPayload.put("RequestPayload", myCustomerJson);
                        mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                    } else {
                        mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "CREATE_CUSTOMER");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                        mapKafkaPayload.put("RequestPayload", myCustomerJson);
                        mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                    }
                } else {
                    mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                    mapKafkaPayload.put("ReferenceNumber", "");
                    mapKafkaPayload.put("MobileNumber", "");
                    mapKafkaPayload.put("RequestType", "CREATE_CUSTOMER");
                    mapKafkaPayload.put("Amount", "0");
                    mapKafkaPayload.put("ResponseCode", Integer.toString(createCustomerDbResp.getResponseCode()));
                    mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(createCustomerDbResp.getResponseMessage()) ? "" : createCustomerDbResp.getResponseMessage()));
                    mapKafkaPayload.put("RequestPayload", myCustomerJson);
                    mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                }
            } else {
                mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                mapKafkaPayload.put("ReferenceNumber", "");
                mapKafkaPayload.put("MobileNumber", "");
                mapKafkaPayload.put("RequestType", "CREATE_CUSTOMER");
                mapKafkaPayload.put("Amount", "0");
                mapKafkaPayload.put("ResponseCode", "1");
                mapKafkaPayload.put("ResponseDescription", "Failed");
                mapKafkaPayload.put("RequestPayload", myCustomerJson);
                mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
            }
                        
            Thread kafkaLog = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(0);
                        kafkaProducer.sendMessage("CustomersLibrary", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                    } catch (InterruptedException v) {
                        logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                    }
                }
            };
            kafkaLog.start();

        } catch (JsonProcessingException ex) {
            logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            custResp.setResponseCode(1);
            custResp.setResponseMessage("Error");
            custResp.setMessageId(suppliedCustData.getMessageId());
            custResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(custResp);
        }
        return retObj;
    }

    
    public Object createAdditionalCustomerWallet(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        CreateAdditionalCustomerWalletResponse custResp = new CreateAdditionalCustomerWalletResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        SuppliedCustomerCreateAdditionalWalletData suppliedCustData = new SuppliedCustomerCreateAdditionalWalletData();
        Functions func = new Functions();
        KafkaLogProducer kafkaProducer = new KafkaLogProducer(commonDataReq.getKafkaConfigs().getServer(), commonDataReq.getKafkaConfigs().getClientId(), commonDataReq.getKafkaConfigs().getTopic());
        
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedCustData = mapper.readValue(myCustomerJson, SuppliedCustomerCreateAdditionalWalletData.class);

            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedCustData.getApiUser(), suppliedCustData.getApiPassword());
            if (validApiUser <= 0){
                custResp.setResponseCode(1);
                custResp.setResponseMessage("API User not allowed");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }
            
            //validations if any
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getDocumentNumber())) {
                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Document Number");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }

            String makerdate;
            String createDate = suppliedCustData.getRequestBody().getMakerDateTime();
            try {
                Date createdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createDate);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                makerdate = formatter.format(createdDate);
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(CustomersLibrary.class.getName()).log(Level.SEVERE, null, ex);

                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Maker Date");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }
            
            String approvedate;
            String approverDate = suppliedCustData.getRequestBody().getApproverDateTime();
            try {
                Date checkerDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(approverDate);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                approvedate = formatter.format(checkerDate);
            } catch (ParseException ex) {
                java.util.logging.Logger.getLogger(CustomersLibrary.class.getName()).log(Level.SEVERE, null, ex);

                custResp.setResponseCode(1);
                custResp.setResponseMessage("Check the Approver Date");
                custResp.setMessageId(suppliedCustData.getMessageId());
                custResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(custResp);
                return retObj;
            }

            CreateAdditionalCustomerWalletResponseData custData = new CreateAdditionalCustomerWalletResponseData();
            CreateAdditionalCustomerWalletDbResp createCustomerDbResp = new CreateAdditionalCustomerWalletDbResp();

            Db db = new Db(commonDataReq.getMwalletDataSource());
            createCustomerDbResp = db.createAdditionalCustomerWallet(
                    validApiUser,
                    suppliedCustData.getRequestBody().getProductCode(),
                    suppliedCustData.getRequestBody().getDocumentNumber(),
                    suppliedCustData.getRequestBody().getMakerUserId(), makerdate,
                    suppliedCustData.getRequestBody().getApproverUserId(), approvedate,
                    suppliedCustData.getRequestBody().getBranchCode()
            );

            custData.setCustomerNumber(createCustomerDbResp.getCustomerNumber());
            custData.setAccountNumber(createCustomerDbResp.getAccountNumber());
            custData.setAccountId(createCustomerDbResp.getAccountId());
            custResp.setResponseCode(createCustomerDbResp.getResponseCode());
            custResp.setResponseMessage(createCustomerDbResp.getResponseMessage());
            custResp.setMessageId(suppliedCustData.getMessageId());
            custResp.setResponseBody(custData);

            retObj = Obj.writeValueAsString(custResp);
            
            HashMap<String, String> mapKafkaPayload = new HashMap<>();
            final String messageId = suppliedCustData.getMessageId();
            if (!func.isNullOrEmpty(Integer.toString(createCustomerDbResp.getResponseCode()))) {
                if (createCustomerDbResp.getResponseCode() == 0) {
                    if (!func.isNullOrEmpty(createCustomerDbResp.getResponseMessage())){
                        mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", suppliedCustData.getMessageId());
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "ADD_CUSTOMER_WALLET");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", Integer.toString(createCustomerDbResp.getResponseCode()));
                        mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(createCustomerDbResp.getResponseMessage()) ? "" : createCustomerDbResp.getResponseMessage()));
                        mapKafkaPayload.put("RequestPayload", myCustomerJson);
                        mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                    } else {
                        mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "ADD_CUSTOMER_WALLET");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                        mapKafkaPayload.put("RequestPayload", myCustomerJson);
                        mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                    }
                } else {
                    mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                    mapKafkaPayload.put("ReferenceNumber", "");
                    mapKafkaPayload.put("MobileNumber", "");
                    mapKafkaPayload.put("RequestType", "ADD_CUSTOMER_WALLET");
                    mapKafkaPayload.put("Amount", "0");
                    mapKafkaPayload.put("ResponseCode", Integer.toString(createCustomerDbResp.getResponseCode()));
                    mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(createCustomerDbResp.getResponseMessage()) ? "" : createCustomerDbResp.getResponseMessage()));
                    mapKafkaPayload.put("RequestPayload", myCustomerJson);
                mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
                }
            } else {
                mapKafkaPayload.put("SessionId", suppliedCustData.getMessageId());
                mapKafkaPayload.put("ReferenceNumber", "");
                mapKafkaPayload.put("MobileNumber", "");
                mapKafkaPayload.put("RequestType", "ADD_CUSTOMER_WALLET");
                mapKafkaPayload.put("Amount", "0");
                mapKafkaPayload.put("ResponseCode", "1");
                mapKafkaPayload.put("ResponseDescription", "Failed");
                mapKafkaPayload.put("RequestPayload", myCustomerJson);
                mapKafkaPayload.put("ChannelId", suppliedCustData.getChannelId());
            }
                        
            Thread kafkaLog = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(0);
                        kafkaProducer.sendMessage("CustomersLibrary", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                    } catch (InterruptedException v) {
                        logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                    }
                }
            };
            kafkaLog.start();

        } catch (JsonProcessingException ex) {
            logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            custResp.setResponseCode(1);
            custResp.setResponseMessage("Error");
            custResp.setMessageId(suppliedCustData.getMessageId());
            custResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(custResp);
        }
        return retObj;
    }
    
    
    public Object editCustomer(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        EditCustomerResponse editResp = new EditCustomerResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        SuppliedCustomerEditData suppliedCustEditData = new SuppliedCustomerEditData();
        Functions func = new Functions();
        KafkaLogProducer kafkaProducer = new KafkaLogProducer(commonDataReq.getKafkaConfigs().getServer(), commonDataReq.getKafkaConfigs().getClientId(), commonDataReq.getKafkaConfigs().getTopic());
        
        EditCustomerResponseData respBody = new EditCustomerResponseData();
        respBody.setStatusDescription("");
        
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedCustEditData = mapper.readValue(myCustomerJson, SuppliedCustomerEditData.class);

            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedCustEditData.getApiUser(), suppliedCustEditData.getApiPassword());
            if (validApiUser <= 0){
                editResp.setResponseCode(1);
                editResp.setResponseMessage("API User not allowed");
                editResp.setMessageId(suppliedCustEditData.getMessageId());
                respBody.setStatusDescription("Check Validations");
                editResp.setResponseBody(respBody);
                retObj = Obj.writeValueAsString(editResp);
                return retObj;
            }
            
            //validations if any
            if (!func.validateDateTime(suppliedCustEditData.getRequestDateTime())){
                editResp.setResponseCode(1);
                editResp.setResponseMessage("Check the Request Date");
                editResp.setMessageId(suppliedCustEditData.getMessageId());
                respBody.setStatusDescription("Check Validations");
                editResp.setResponseBody(respBody);
                retObj = Obj.writeValueAsString(editResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedCustEditData.getRequestDateTime())){
                    editResp.setResponseCode(1);
                    editResp.setResponseMessage("Check the Request Date Must be today");
                    editResp.setMessageId(suppliedCustEditData.getMessageId());
                    respBody.setStatusDescription("Check Validations");
                    editResp.setResponseBody(respBody);
                    retObj = Obj.writeValueAsString(editResp);
                    return retObj;
                }
            }
            
            String dob;
            if (!func.validateDate(suppliedCustEditData.getRequestBody().getDateOfBirth())){
                editResp.setResponseCode(1);
                editResp.setResponseMessage("Check the Date of Birth");
                editResp.setMessageId(suppliedCustEditData.getMessageId());
                respBody.setStatusDescription("Check Validations");
                editResp.setResponseBody(respBody);
                retObj = Obj.writeValueAsString(editResp);
                return retObj;
            } else {
                if (!func.validateDob(new SimpleDateFormat("dd-MM-yyyy").format(new Date()), suppliedCustEditData.getRequestBody().getDateOfBirth())){
                    editResp.setResponseCode(1);
                    editResp.setResponseMessage("Check the Date of Birth");
                    editResp.setMessageId(suppliedCustEditData.getMessageId());
                    respBody.setStatusDescription("Check Validations");
                    editResp.setResponseBody(respBody);
                    retObj = Obj.writeValueAsString(editResp);
                    return retObj;
                }
                dob = func.formatDob(suppliedCustEditData.getRequestBody().getDateOfBirth());
            }
            //if email is supplied then validate it
            if (!func.isNullOrEmpty(suppliedCustEditData.getRequestBody().getEmailAddress())) {
                boolean isValidEmailAddress = func.emailValidate(suppliedCustEditData.getRequestBody().getEmailAddress());
                if (!isValidEmailAddress) {
                    editResp.setResponseCode(1);
                    editResp.setResponseMessage("Check the Email");
                    editResp.setMessageId(suppliedCustEditData.getMessageId());
                    editResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(editResp);
                    return retObj;
                }
            }
            
            EditCustomerDbResp editCustomerDbResp = new EditCustomerDbResp();
            Db db = new Db(commonDataReq.getMwalletDataSource());
            
            editCustomerDbResp = db.editCustomer(
                    validApiUser, dob, suppliedCustEditData
            );
            
            editResp.setResponseCode(editCustomerDbResp.getResponseCode());
            editResp.setResponseMessage(editCustomerDbResp.getResponseMessage());
            editResp.setMessageId(editCustomerDbResp.getMessageId());
            respBody.setStatusDescription(editCustomerDbResp.getStatusDescription());
            editResp.setResponseBody(respBody);
            retObj = Obj.writeValueAsString(editResp);
            
            HashMap<String, String> mapKafkaPayload = new HashMap<>();
            final String messageId = suppliedCustEditData.getMessageId();
            if (!func.isNullOrEmpty(Integer.toString(editCustomerDbResp.getResponseCode()))) {
                if (editCustomerDbResp.getResponseCode() == 0) {
                    if (!func.isNullOrEmpty(editCustomerDbResp.getResponseMessage())){
                        mapKafkaPayload.put("SessionId", suppliedCustEditData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", suppliedCustEditData.getMessageId());
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "EDIT_CUSTOMER");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", Integer.toString(editCustomerDbResp.getResponseCode()));
                        mapKafkaPayload.put("ResponseDescription", "Successful: " + (func.isNullOrEmpty(editCustomerDbResp.getResponseMessage()) ? "" : editCustomerDbResp.getResponseMessage()));
                        mapKafkaPayload.put("RequestPayload", myCustomerJson);
                        mapKafkaPayload.put("ChannelId", suppliedCustEditData.getChannelId());
                    } else {
                        mapKafkaPayload.put("SessionId", suppliedCustEditData.getMessageId());
                        mapKafkaPayload.put("ReferenceNumber", "");
                        mapKafkaPayload.put("MobileNumber", "");
                        mapKafkaPayload.put("RequestType", "EDIT_CUSTOMER");
                        mapKafkaPayload.put("Amount", "0");
                        mapKafkaPayload.put("ResponseCode", "1");
                        mapKafkaPayload.put("ResponseDescription", "Failed");
                        mapKafkaPayload.put("RequestPayload", myCustomerJson);
                        mapKafkaPayload.put("ChannelId", suppliedCustEditData.getChannelId());
                    }
                } else {
                    mapKafkaPayload.put("SessionId", suppliedCustEditData.getMessageId());
                    mapKafkaPayload.put("ReferenceNumber", "");
                    mapKafkaPayload.put("MobileNumber", "");
                    mapKafkaPayload.put("RequestType", "EDIT_CUSTOMER");
                    mapKafkaPayload.put("Amount", "0");
                    mapKafkaPayload.put("ResponseCode", Integer.toString(editCustomerDbResp.getResponseCode()));
                    mapKafkaPayload.put("ResponseDescription", "Failed : " + (func.isNullOrEmpty(editCustomerDbResp.getResponseMessage()) ? "" : editCustomerDbResp.getResponseMessage()));
                    mapKafkaPayload.put("RequestPayload", myCustomerJson);
                    mapKafkaPayload.put("ChannelId", suppliedCustEditData.getChannelId());
                }
            } else {
                mapKafkaPayload.put("SessionId", suppliedCustEditData.getMessageId());
                mapKafkaPayload.put("ReferenceNumber", "");
                mapKafkaPayload.put("MobileNumber", "");
                mapKafkaPayload.put("RequestType", "EDIT_CUSTOMER");
                mapKafkaPayload.put("Amount", "0");
                mapKafkaPayload.put("ResponseCode", "1");
                mapKafkaPayload.put("ResponseDescription", "Failed");
                mapKafkaPayload.put("RequestPayload", myCustomerJson);
                mapKafkaPayload.put("ChannelId", suppliedCustEditData.getChannelId());
            }
                        
            Thread kafkaLog = new Thread() {
                public void run() {
                    try {
                        Thread.sleep(0);
                        kafkaProducer.sendMessage("CustomersLibrary", commonDataReq.getKafkaConfigs().getTopic(), messageId, mapKafkaPayload);
                    } catch (InterruptedException v) {
                        logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + v, v);
                    }
                }
            };
            kafkaLog.start();
            
        } catch (Exception ex) {
            logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            editResp.setResponseCode(-1);
            editResp.setResponseMessage("Error");
            editResp.setMessageId(suppliedCustEditData.getMessageId());
            respBody.setStatusDescription("Error");
            editResp.setResponseBody(respBody);
            retObj = Obj.writeValueAsString(editResp);
        }
        return retObj;
    }
    
    
    public Object checkIfEmailExists(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        CheckEmailResponse emailCheckResp = new CheckEmailResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        SuppliedCheckEmailData suppliedEmailData = new SuppliedCheckEmailData();
        Functions func = new Functions();
        
        try {
            CheckEmailResponseData respBody = new CheckEmailResponseData();
            CheckEmailDbResp emailCheck = new CheckEmailDbResp();
            
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedEmailData = mapper.readValue(myCustomerJson, SuppliedCheckEmailData.class);
            
            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedEmailData.getApiUser(), suppliedEmailData.getApiPassword());
            if (validApiUser <= 0){
                emailCheckResp.setResponseCode(1);
                emailCheckResp.setResponseMessage("API User not allowed");
                emailCheckResp.setMessageId(suppliedEmailData.getMessageId());
                emailCheckResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(emailCheckResp);
                return retObj;
            }

            //validations if any
            if (!func.validateDateTime(suppliedEmailData.getRequestDateTime())){
                emailCheckResp.setResponseCode(1);
                emailCheckResp.setResponseMessage("Check the Request Date");
                emailCheckResp.setMessageId(suppliedEmailData.getMessageId());
                emailCheckResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(emailCheckResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedEmailData.getRequestDateTime())){
                    emailCheckResp.setResponseCode(1);
                    emailCheckResp.setResponseMessage("Check the Request Date Must be today");
                    emailCheckResp.setMessageId(suppliedEmailData.getMessageId());
                    emailCheckResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(emailCheckResp);
                    return retObj;
                }
            }
            
            double custId = 0;
            String emailAddress = "";
            
            if (func.isNullOrEmpty(suppliedEmailData.getRequestBody().getEmailAddress())) {
                emailCheckResp.setResponseCode(1);
                emailCheckResp.setResponseMessage("Check the Search Criteria");
                emailCheckResp.setMessageId(suppliedEmailData.getMessageId());
                emailCheckResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(emailCheckResp);
                return retObj;
            }
            
            if (func.isNullOrEmpty(Double.toString(suppliedEmailData.getRequestBody().getCustomerId()))) {
                custId = 0;
            } else {
                custId = suppliedEmailData.getRequestBody().getCustomerId();
            }
            
            if (func.isNullOrEmpty(suppliedEmailData.getRequestBody().getEmailAddress())) {
                emailAddress = "";
            } else {
                emailAddress = suppliedEmailData.getRequestBody().getEmailAddress();
            }

            CheckEmailDbResp checkEmailDb = null;
            //CheckEmailResponseData custData = new CheckEmailResponseData();
            
            Db db = new Db(commonDataReq.getMwalletDataSource());
            checkEmailDb = db.checkEmailAddress(
                validApiUser, custId, emailAddress
            );
            
            if (!checkEmailDb.getStatusCode().isEmpty() && !checkEmailDb.getStatusMessage().isEmpty()) {
                emailCheckResp.setResponseCode(Integer.parseInt(checkEmailDb.getStatusCode()));
                emailCheckResp.setResponseMessage(checkEmailDb.getStatusMessage());
                emailCheckResp.setMessageId(suppliedEmailData.getMessageId());
                
                emailCheck.setStatusCode(checkEmailDb.getStatusCode());
                emailCheck.setStatusMessage(checkEmailDb.getStatusMessage());
                respBody.setEmailCheck(emailCheck);
                emailCheckResp.setResponseBody(respBody);
            } else {
                emailCheckResp.setResponseCode(1);
                emailCheckResp.setResponseMessage("Error Occured while trying to get email");
                emailCheckResp.setMessageId(suppliedEmailData.getMessageId());
                
                emailCheck.setStatusCode("1");
                emailCheck.setStatusMessage("Error Occured while trying to get email");
                respBody.setEmailCheck(emailCheck);
                emailCheckResp.setResponseBody(respBody);
            }

            retObj = Obj.writeValueAsString(emailCheckResp);

            
            
        } catch (Exception ex) {
            logger.error(CustomersLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            emailCheckResp.setResponseCode(1);
            emailCheckResp.setResponseMessage("Error");
            emailCheckResp.setMessageId(suppliedEmailData.getMessageId());
            emailCheckResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(emailCheckResp);
        }
        return retObj;
    }
    
}
