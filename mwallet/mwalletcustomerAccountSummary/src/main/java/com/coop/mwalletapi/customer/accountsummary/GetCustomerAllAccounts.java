/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coop.mwalletapi.customer.accountsummary;

import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import com.coop.mwalletapi.customer.accountsummary.dao.Db;
import com.coop.mwalletapi.customer.accountsummary.dao.model.GetCustomerAllAccountsDbResp;
import com.coop.mwalletapi.customer.get.allaccounts.req.SuppliedCustomerGetAllAccountsData;
import com.coop.mwalletapi.customer.get.allaccounts.resp.GetCustomerAllAccountsResp;
import com.coop.mwalletapi.customer.get.allaccounts.resp.GetCustomerAllAccountsResponse;
import com.coop.mwalletapi.customer.get.allaccounts.resp.GetCustomerAllAccountsResponseData;
import com.coop.mwalletapi.functions.Functions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author okahia
 */
public class GetCustomerAllAccounts {
    private static final Logger logger = LogManager.getLogger(GetCustomerAllAccounts.class.getName());
    
    public Object getCustomerAllAccount(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        GetCustomerAllAccountsResponse custResp = new GetCustomerAllAccountsResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        SuppliedCustomerGetAllAccountsData suppliedCustData = new SuppliedCustomerGetAllAccountsData();
        Functions func = new Functions();
        
        try {
//            GetCustomerAllAccountsResponse customerDataDbResp = new GetCustomerAllAccountsResponse();
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(GetCustomerAllAccounts.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedCustData = mapper.readValue(myCustomerJson, SuppliedCustomerGetAllAccountsData.class);
            
//            //get entity id from valid api user
              int validApiUser = 0;
//            validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedCustData.getApiUser(), suppliedCustData.getApiPassword());
//            if (validApiUser <= 0){
//                custResp.setResponseCode(1);
//                custResp.setResponseMessage("API User not allowed");
//                custResp.setMessageId(suppliedCustData.getMessageId());
//                custResp.setResponseBody(null);
//                retObj = Obj.writeValueAsString(custResp);
//                return retObj;
//            }

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
            
            String documentNo = ""; String mobileNo = ""; String customerNo = "";
            
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
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getDocumentNo())) {
                documentNo = "";
            } else {
                documentNo = suppliedCustData.getRequestBody().getDocumentNo();
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getMobileNo())) {
                mobileNo = "";
            } else {
                mobileNo = suppliedCustData.getRequestBody().getMobileNo();
            }
            
            if (func.isNullOrEmpty(suppliedCustData.getRequestBody().getCustomerNo())) {
                customerNo = "";
            } else {
                customerNo = suppliedCustData.getRequestBody().getCustomerNo();
            }

            
            GetCustomerAllAccountsDbResp custData = new GetCustomerAllAccountsDbResp();
            
            
            Db db = new Db(commonDataReq.getMwalletDataSource());
            custData = db.getCustomerAllAccountsData(
                    validApiUser,
                    documentNo,
                    mobileNo,
                    customerNo
            );
            

            GetCustomerAllAccountsResponseData customerDataDbResp = new GetCustomerAllAccountsResponseData();
            List<GetCustomerAllAccountsResp> accountsData = new ArrayList<>();
            

            if (custData.getResponseCode() == 0) {
                if (!custData.getAccountsData().isEmpty()) {
                    custResp.setResponseCode(custData.getResponseCode());
                    custResp.setResponseMessage(custData.getResponseMessage());
                    custResp.setMessageId(suppliedCustData.getMessageId());
                    
                    for (int i = 0; i < custData.getAccountsData().size(); i++) {
                        if (i == 0){
                            customerDataDbResp.setCustomerName(custData.getAccountsData().get(0).getAccountName());
                            customerDataDbResp.setDocumentNo(custData.getAccountsData().get(0).getDocumentNo());
                            customerDataDbResp.setMobileNo(custData.getAccountsData().get(0).getMobileNo());
                            customerDataDbResp.setCustomerNo(custData.getAccountsData().get(0).getCustomerNumber());
                        }
                        GetCustomerAllAccountsResp accountsAllResp = new GetCustomerAllAccountsResp();
                        accountsAllResp.setAccountName(custData.getAccountsData().get(i).getAccountName());
                        accountsAllResp.setAccountNumber(custData.getAccountsData().get(i).getAccountNumber());
                        accountsAllResp.setAccountAlias(custData.getAccountsData().get(i).getAccountAlias());
                        accountsAllResp.setEntityId(custData.getAccountsData().get(i).getEntityId());
                        accountsAllResp.setEntityName(custData.getAccountsData().get(i).getEntityName());
                        accountsAllResp.setClearedBalance(func.formatAmounts(Double.parseDouble(custData.getAccountsData().get(i).getClearedBalance())));
                        accountsAllResp.setBlockedBalance(func.formatAmounts(Double.parseDouble(custData.getAccountsData().get(i).getBlockedBalance())));
                        accountsAllResp.setAvailableBalance(func.formatAmounts(Double.parseDouble(custData.getAccountsData().get(i).getAvailableBalance())));
                        accountsAllResp.setCurrency(custData.getAccountsData().get(i).getCurrency());
                        accountsAllResp.setProductCode(custData.getAccountsData().get(i).getProductCode());
                        accountsAllResp.setProductName(custData.getAccountsData().get(i).getProductName());
                        accountsAllResp.setBranchcode(custData.getAccountsData().get(i).getBranchcode());
                        accountsAllResp.setClosed(custData.getAccountsData().get(i).getClosed());
                        accountsAllResp.setDormant(custData.getAccountsData().get(i).getDormant());
                        accountsAllResp.setDebitLimit(func.formatAmounts(Double.parseDouble(custData.getAccountsData().get(i).getDebitLimit())));
                        accountsAllResp.setCreditLimit(func.formatAmounts(Double.parseDouble(custData.getAccountsData().get(i).getCreditLimit())));
                        accountsAllResp.setDebitAllowed(custData.getAccountsData().get(i).getDebitAllowed());
                        accountsAllResp.setCreditAllowed(custData.getAccountsData().get(i).getCreditAllowed());
                        accountsAllResp.setMaximumBalance(func.formatAmounts(Double.parseDouble(custData.getAccountsData().get(i).getMaximumBalance())));
                        accountsAllResp.setCreatedOn(custData.getAccountsData().get(i).getCreatedOn());
                        accountsAllResp.setCreatedBy(custData.getAccountsData().get(i).getCreatedBy());
                        accountsAllResp.setAuthorisedOn(custData.getAccountsData().get(i).getAuthorisedOn());
                        accountsAllResp.setAuthorisedBy(custData.getAccountsData().get(i).getAuthorisedBy());
                        
                        accountsAllResp.setStatus(custData.getAccountsData().get(i).getStatus());
                        accountsAllResp.setStatusDesc(custData.getAccountsData().get(i).getStatusDesc());
                        
                        accountsData.add(accountsAllResp);
                    }
                    customerDataDbResp.setAccountsData(accountsData);
                    custResp.setResponseBody(customerDataDbResp);
                } else {
                    custResp.setResponseCode(custData.getResponseCode());
                    custResp.setResponseMessage(custData.getResponseMessage());
                    custResp.setMessageId(suppliedCustData.getMessageId());
                    custResp.setResponseBody(customerDataDbResp);
                }
            }
            retObj = Obj.writeValueAsString(custResp);
            
        } catch (Exception ex) {
            logger.error(GetCustomerAllAccounts.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            custResp.setResponseCode(1);
            custResp.setResponseMessage("Error");
            custResp.setMessageId(suppliedCustData.getMessageId());
            custResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(custResp);
        }
        return retObj;
    }
}
