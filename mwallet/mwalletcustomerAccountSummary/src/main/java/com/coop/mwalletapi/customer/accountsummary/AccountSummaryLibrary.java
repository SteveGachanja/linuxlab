package com.coop.mwalletapi.customer.accountsummary;

import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import com.coop.mwalletapi.customer.accountsummary.dao.Db;
import com.coop.mwalletapi.customer.accountsummary.dao.model.AccountDataDbDao;
import com.coop.mwalletapi.customer.accountsummary.dao.model.AccountDataDbResp;
import com.coop.mwalletapi.customer.accountsummary.entities.AccountResponse;
import com.coop.mwalletapi.customer.accountsummary.entities.AccountResponseData;
import com.coop.mwalletapi.customer.accountsummary.entities.InputAccountRequest;
import com.coop.mwalletapi.functions.Functions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author pkingongo
 */

public class AccountSummaryLibrary {
    private static final Logger logger = LogManager.getLogger(AccountSummaryLibrary.class.getName());
    
 
    
    public Object getCustomerAccounts(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        AccountResponse accountResp = new AccountResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
         Functions func = new Functions();
        try {
            InputAccountRequest inputAccountRequest = new InputAccountRequest();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(AccountSummaryLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            inputAccountRequest = mapper.readValue(myCustomerJson, InputAccountRequest.class);
            
             accountResp.setMessageId(inputAccountRequest.getMessageId());
            //validate credentials
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), inputAccountRequest.getApiUser(), inputAccountRequest.getApiPassword());
            if (validApiUser <= 0){
                accountResp.setResponseCode(1);
                accountResp.setResponseMessage("API User not allowed");
                accountResp.setMessageId(inputAccountRequest.getMessageId());
                accountResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(accountResp);
                return retObj;
            }
            
            //Do the processing here
            //validations if any
        
            if (func.isNullOrEmpty(inputAccountRequest.getRequestBody().getDocumentNo())) {
                accountResp.setResponseCode(1);
                accountResp.setResponseMessage("Check the Document Number");
               
                accountResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(accountResp);
                return retObj;
            }
           
            
            List<AccountDataDbDao> accountDataDbDao = null;
            AccountResponseData accountData = new AccountResponseData();
            AccountDataDbResp accountDataDbResp = new AccountDataDbResp();
            
            Db db = new Db(commonDataReq.getMwalletDataSource());
            accountDataDbResp = db.getMClientData(inputAccountRequest.getRequestBody().getDocumentNo(),validApiUser);
            if (!accountDataDbResp.getAccountData().isEmpty()) {
                accountData.setAccountData(accountDataDbResp.getAccountData());
                accountResp.setResponseCode(accountDataDbResp.getResponseCode());
                accountResp.setResponseMessage(accountDataDbResp.getResponseMessage());
                accountResp.setResponseBody(accountData);
            } else {
                accountData.setAccountData(accountDataDbDao);
                accountResp.setResponseCode(accountDataDbResp.getResponseCode());
                accountResp.setResponseMessage(accountDataDbResp.getResponseMessage());
                accountResp.setResponseBody(accountData);
            }
           
            retObj = Obj.writeValueAsString(accountResp);
            
        } catch (Exception ex) {
            logger.error(AccountSummaryLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
            
            accountResp.setResponseCode(0);
            accountResp.setResponseMessage("Error");
            accountResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(accountResp);
        }
        return retObj;
    }
}
