package com.coop.mwalletapi.adminlibrary;

import com.coop.mwalletapi.adminlibrary.dao.Db;
import com.coop.mwalletapi.adminlibrary.dao.model.CreateApiUserDbResp;
import com.coop.mwalletapi.adminlibrary.dao.model.GetApiUserDataDbResp;
import com.coop.mwalletapi.adminlibrary.dao.model.GetNationalitiesDataDbResp;
import com.coop.mwalletapi.adminlibrary.dao.model.GetProductsDataDbResp;
import com.coop.mwalletapi.adminlibrary.dao.model.GetSegmentsDataDbResp;
import com.coop.mwalletapi.adminlibrary.entities.createapiuser.SuppliedApiUserCreateData;
import com.coop.mwalletapi.adminlibrary.entities.createapiuser.response.CreateApiUserResponse;
import com.coop.mwalletapi.adminlibrary.entities.createapiuser.response.CreateApiUserResponseData;
import com.coop.mwalletapi.adminlibrary.entities.getapiuser.SuppliedApiGetUserData;
import com.coop.mwalletapi.adminlibrary.entities.getapiuser.response.GetApiUsersResponse;
import com.coop.mwalletapi.adminlibrary.entities.getapiuser.response.GetApiUsersResponseData;
import com.coop.mwalletapi.adminlibrary.entities.getnationalities.SuppliedApiGetNationalitiesData;
import com.coop.mwalletapi.adminlibrary.entities.getnationalities.response.GetNationalitiesResponse;
import com.coop.mwalletapi.adminlibrary.entities.getnationalities.response.GetNationalitiesResponseData;
import com.coop.mwalletapi.adminlibrary.entities.getproducts.SuppliedApiGetProductsData;
import com.coop.mwalletapi.adminlibrary.entities.getproducts.response.GetProductsResponse;
import com.coop.mwalletapi.adminlibrary.entities.getproducts.response.GetProductsResponseData;
import com.coop.mwalletapi.adminlibrary.entities.getsegments.SuppliedApiGetSegmentsData;
import com.coop.mwalletapi.adminlibrary.entities.getsegments.response.GetSegmentsResponse;
import com.coop.mwalletapi.adminlibrary.entities.getsegments.response.GetSegmentsResponseData;
import com.coop.mwalletapi.commons.ApiUsers;
import com.coop.mwalletapi.functions.Functions;
import com.coop.mwalletapi.commons.MwalletCommonDataReq;
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
public class AdminLibrary {
    
    private static final Logger logger = LogManager.getLogger(AdminLibrary.class.getName());
    
    public Object createApiUser(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        CreateApiUserResponse userCreateResp = new CreateApiUserResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        SuppliedApiUserCreateData suppliedApiCreateData = new SuppliedApiUserCreateData();
        Functions func = new Functions();
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedApiCreateData = mapper.readValue(myCustomerJson, SuppliedApiUserCreateData.class);

            //validations if any
            if (func.isNullOrEmpty(Integer.toString(suppliedApiCreateData.getRequestBody().getEntityId()))) {
                userCreateResp.setResponseCode(1);
                userCreateResp.setResponseMessage("Check the Entity");
                userCreateResp.setMessageId(suppliedApiCreateData.getMessageId());
                userCreateResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(userCreateResp);
                return retObj;
            }
            if (func.isNullOrEmpty(suppliedApiCreateData.getRequestBody().getUserName())) {
                userCreateResp.setResponseCode(1);
                userCreateResp.setResponseMessage("Check the user name");
                userCreateResp.setMessageId(suppliedApiCreateData.getMessageId());
                userCreateResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(userCreateResp);
                return retObj;
            }
            if (func.isNullOrEmpty(suppliedApiCreateData.getRequestBody().getPassword())) {
                userCreateResp.setResponseCode(1);
                userCreateResp.setResponseMessage("Check the Password");
                userCreateResp.setMessageId(suppliedApiCreateData.getMessageId());
                userCreateResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(userCreateResp);
                return retObj;
            } else {
                //Check the password passed meets this standards:
                //(0-9) a digit must occur at least once
                //(a-z) a lower case letter must occur at least once
                //(A-Z) an upper case letter must occur at least once
                //(@#$%^&+=) a special character must occur at least once
                //(\S) no whitespace allowed in the entire string
                //(8) at least 8 characters
                
                boolean isValidPassword = func.passwordValidate(suppliedApiCreateData.getRequestBody().getPassword());
                if (!isValidPassword) {
                    userCreateResp.setResponseCode(1);
                    userCreateResp.setResponseMessage("Check the password strength");
                    userCreateResp.setMessageId(suppliedApiCreateData.getMessageId());
                    userCreateResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(userCreateResp);
                    return retObj;
                }
            }

            

            CreateApiUserResponseData custData = new CreateApiUserResponseData();
            CreateApiUserDbResp createApiUserDbResp = new CreateApiUserDbResp();

            Db db = new Db(commonDataReq.getMwalletDataSource());
            createApiUserDbResp = db.createApiUserInDb(
                    suppliedApiCreateData.getRequestBody().getEntityId(), 
                    suppliedApiCreateData.getRequestBody().getUserName(),
                    func.encodeString(suppliedApiCreateData.getRequestBody().getPassword()),
                    suppliedApiCreateData.getApiUser()
            );

            custData.setApiUser(createApiUserDbResp.getApiUserId());
            custData.setApiUserName(createApiUserDbResp.getApiUserName());
            userCreateResp.setResponseCode(createApiUserDbResp.getResponseCode());
            userCreateResp.setResponseMessage(createApiUserDbResp.getResponseMessage());
            userCreateResp.setMessageId(suppliedApiCreateData.getMessageId());
            userCreateResp.setResponseBody(custData);

            retObj = Obj.writeValueAsString(userCreateResp);

        } catch (JsonProcessingException ex) {
            logger.error(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            userCreateResp.setResponseCode(1);
            userCreateResp.setResponseMessage("Error");
            userCreateResp.setMessageId(suppliedApiCreateData.getMessageId());
            userCreateResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(userCreateResp);
        }
        return retObj;
    }
    
    
    public Object getApiUsers(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        GetApiUsersResponse getUserResp = new GetApiUsersResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        SuppliedApiGetUserData suppliedGetApiUsersData = new SuppliedApiGetUserData();
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedGetApiUsersData = mapper.readValue(myCustomerJson, SuppliedApiGetUserData.class);

            GetApiUsersResponseData usersData = new GetApiUsersResponseData();
            GetApiUserDataDbResp getApiUserDbResp = new GetApiUserDataDbResp();

            getApiUserDbResp = getApiUsersFromDb(commonDataReq);

            usersData.setApiUsers(getApiUserDbResp.getApiUsers());
            getUserResp.setResponseCode(getApiUserDbResp.getResponseCode());
            getUserResp.setResponseMessage(getApiUserDbResp.getResponseMessage());
            getUserResp.setMessageId(suppliedGetApiUsersData.getMessageId());
            getUserResp.setResponseBody(usersData);

            retObj = Obj.writeValueAsString(getUserResp);

        } catch (JsonProcessingException ex) {
            logger.error(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            getUserResp.setResponseCode(1);
            getUserResp.setResponseMessage("Error");
            getUserResp.setMessageId(suppliedGetApiUsersData.getMessageId());
            getUserResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(getUserResp);
        }
        return retObj;
    }
    
    public List<ApiUsers> getApiUsersList(MwalletCommonDataReq commonDataReq){
        List<ApiUsers> retApiUsers = new ArrayList<ApiUsers>();
        try {
            GetApiUserDataDbResp getApiUserDbResp = new GetApiUserDataDbResp();
            getApiUserDbResp = getApiUsersFromDb(commonDataReq);
            if (getApiUserDbResp != null){
                for (int i = 0; i < getApiUserDbResp.getApiUsers().size(); i++) {
                    
                    ApiUsers apiUsr = new ApiUsers();
                    apiUsr.setUserId(getApiUserDbResp.getApiUsers().get(i).getUserId());
                    apiUsr.setUserName(getApiUserDbResp.getApiUsers().get(i).getUserName());
                    apiUsr.setEntityId(getApiUserDbResp.getApiUsers().get(i).getEntityId());
                    apiUsr.setPassword(getApiUserDbResp.getApiUsers().get(i).getPassword());
                    apiUsr.setStatus(getApiUserDbResp.getApiUsers().get(i).getStatus());
                
                    retApiUsers.add(apiUsr);
                }
            }
        } catch (Exception ex) {
            logger.error(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return retApiUsers;
    }
    
    private GetApiUserDataDbResp getApiUsersFromDb(MwalletCommonDataReq commonDataReq){
        //ApiUsers retApiUsers = new ApiUsers();
        GetApiUserDataDbResp getApiUserDbResp = new GetApiUserDataDbResp();
        try {
            Db db = new Db(commonDataReq.getMwalletDataSource());
            getApiUserDbResp = db.getApiUsersFromDb();
        
        } catch (Exception ex) {
            logger.error(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        
        return getApiUserDbResp;
    }
    
    
    public Object getNationalities(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        GetNationalitiesResponse getNationalitiesResp = new GetNationalitiesResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        SuppliedApiGetNationalitiesData suppliedGetNationlitiesData = new SuppliedApiGetNationalitiesData();
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedGetNationlitiesData = mapper.readValue(myCustomerJson, SuppliedApiGetNationalitiesData.class);

            GetNationalitiesResponseData nationalitiesData = new GetNationalitiesResponseData();
            GetNationalitiesDataDbResp getNationalitiesDbResp = new GetNationalitiesDataDbResp();
            
            String SearchByName = func.isNullOrEmpty(suppliedGetNationlitiesData.getRequestBody().getSearchByName()) ? "" : suppliedGetNationlitiesData.getRequestBody().getSearchByName().toUpperCase();
            String SearchByCode = func.isNullOrEmpty(suppliedGetNationlitiesData.getRequestBody().getSearchByCode()) ? "" : suppliedGetNationlitiesData.getRequestBody().getSearchByCode().toUpperCase();
            
            if (SearchByName.length() > 0 && SearchByCode.length() > 0) {
                getNationalitiesResp.setResponseCode(1);
                getNationalitiesResp.setResponseMessage("Search Criteria should be one field at a time, either use one of [searchByName or searchByCode]");
                getNationalitiesResp.setMessageId(suppliedGetNationlitiesData.getMessageId());
                getNationalitiesResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(getNationalitiesResp);
                return retObj;
            }

            Db db = new Db(commonDataReq.getMwalletDataSource());
            
            getNationalitiesDbResp = db.getNationalitiesFromDb(SearchByName, SearchByCode);

            nationalitiesData.setNationalities(getNationalitiesDbResp.getNationalities());
            getNationalitiesResp.setResponseCode(getNationalitiesDbResp.getResponseCode());
            getNationalitiesResp.setResponseMessage(getNationalitiesDbResp.getResponseMessage());
            getNationalitiesResp.setMessageId(suppliedGetNationlitiesData.getMessageId());
            getNationalitiesResp.setResponseBody(nationalitiesData);

            retObj = Obj.writeValueAsString(getNationalitiesResp);

        } catch (JsonProcessingException ex) {
            logger.error(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            getNationalitiesResp.setResponseCode(1);
            getNationalitiesResp.setResponseMessage("Error");
            getNationalitiesResp.setMessageId(suppliedGetNationlitiesData.getMessageId());
            getNationalitiesResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(getNationalitiesResp);
        }
        return retObj;
    }
        
    public Object getProducts(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        GetProductsResponse getProductsResp = new GetProductsResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        SuppliedApiGetProductsData suppliedGetProductsData = new SuppliedApiGetProductsData();
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedGetProductsData = mapper.readValue(myCustomerJson, SuppliedApiGetProductsData.class);

            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedGetProductsData.getApiUser(), suppliedGetProductsData.getApiPassword());
            if (validApiUser <= 0){
                getProductsResp.setResponseCode(-1);
                getProductsResp.setResponseMessage("API User not allowed");
                getProductsResp.setMessageId(suppliedGetProductsData.getMessageId());
                getProductsResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(getProductsResp);
                return retObj;
            }
            
            //validations if any
            if (!func.validateDateTime(suppliedGetProductsData.getRequestDateTime())){
                getProductsResp.setResponseCode(-1);
                getProductsResp.setResponseMessage("Check the Request Date");
                getProductsResp.setMessageId(suppliedGetProductsData.getMessageId());
                getProductsResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(getProductsResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedGetProductsData.getRequestDateTime())){
                    getProductsResp.setResponseCode(-1);
                    getProductsResp.setResponseMessage("Check the Request Date Must be today");
                    getProductsResp.setMessageId(suppliedGetProductsData.getMessageId());
                    getProductsResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(getProductsResp);
                    return retObj;
                }
            }
            
            GetProductsResponseData productsData = new GetProductsResponseData();
            GetProductsDataDbResp getProductsDbResp = new GetProductsDataDbResp();
            
            String productCode = func.isNullOrEmpty(suppliedGetProductsData.getRequestBody().getProductCode()) ? "" : suppliedGetProductsData.getRequestBody().getProductCode();

            Db db = new Db(commonDataReq.getMwalletDataSource());
            getProductsDbResp = db.getProductsFromDb(validApiUser, productCode);

            productsData.setProducts(getProductsDbResp.getProducts());
            getProductsResp.setResponseCode(getProductsDbResp.getResponseCode());
            getProductsResp.setResponseMessage(getProductsDbResp.getResponseMessage());
            getProductsResp.setMessageId(suppliedGetProductsData.getMessageId());
            getProductsResp.setResponseBody(productsData);

            retObj = Obj.writeValueAsString(getProductsResp);

        } catch (JsonProcessingException ex) {
            logger.error(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            getProductsResp.setResponseCode(1);
            getProductsResp.setResponseMessage("Error");
            getProductsResp.setMessageId(suppliedGetProductsData.getMessageId());
            getProductsResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(getProductsResp);
        }
        return retObj;
    }
    
    public Object getSegments(MwalletCommonDataReq commonDataReq) throws JsonProcessingException {
        GetSegmentsResponse getSegmentsResp = new GetSegmentsResponse();
        ObjectMapper Obj = new ObjectMapper();
        Object retObj = null;
        Functions func = new Functions();
        SuppliedApiGetSegmentsData suppliedGetSegmentsData = new SuppliedApiGetSegmentsData();
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            String myCustomerJson = commonDataReq.getInObj().toString();
            logger.info(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + myCustomerJson);
            suppliedGetSegmentsData = mapper.readValue(myCustomerJson, SuppliedApiGetSegmentsData.class);

            //get entity id from valid api user
            int validApiUser = func.ValidateApiUser(commonDataReq.getApiUsers(), suppliedGetSegmentsData.getApiUser(), suppliedGetSegmentsData.getApiPassword());
            if (validApiUser <= 0){
                getSegmentsResp.setResponseCode(-1);
                getSegmentsResp.setResponseMessage("API User not allowed");
                getSegmentsResp.setMessageId(suppliedGetSegmentsData.getMessageId());
                getSegmentsResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(getSegmentsResp);
                return retObj;
            }
            
            //validations if any
            if (!func.validateDateTime(suppliedGetSegmentsData.getRequestDateTime())){
                getSegmentsResp.setResponseCode(-1);
                getSegmentsResp.setResponseMessage("Check the Request Date");
                getSegmentsResp.setMessageId(suppliedGetSegmentsData.getMessageId());
                getSegmentsResp.setResponseBody(null);
                retObj = Obj.writeValueAsString(getSegmentsResp);
                return retObj;
            } else {
                if (!func.validateDateIsToday(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), suppliedGetSegmentsData.getRequestDateTime())){
                    getSegmentsResp.setResponseCode(-1);
                    getSegmentsResp.setResponseMessage("Check the Request Date Must be today");
                    getSegmentsResp.setMessageId(suppliedGetSegmentsData.getMessageId());
                    getSegmentsResp.setResponseBody(null);
                    retObj = Obj.writeValueAsString(getSegmentsResp);
                    return retObj;
                }
            }
            
            GetSegmentsResponseData SegmentsData = new GetSegmentsResponseData();
            GetSegmentsDataDbResp getSegmentsDbResp = new GetSegmentsDataDbResp();
            
            String productCode = func.isNullOrEmpty(suppliedGetSegmentsData.getRequestBody().getSegmentCode()) ? "" : suppliedGetSegmentsData.getRequestBody().getSegmentCode();

            Db db = new Db(commonDataReq.getMwalletDataSource());
            getSegmentsDbResp = db.getSegmentsFromDb(validApiUser, productCode);

            SegmentsData.setSegments(getSegmentsDbResp.getSegments());
            getSegmentsResp.setResponseCode(getSegmentsDbResp.getResponseCode());
            getSegmentsResp.setResponseMessage(getSegmentsDbResp.getResponseMessage());
            getSegmentsResp.setMessageId(suppliedGetSegmentsData.getMessageId());
            getSegmentsResp.setResponseBody(SegmentsData);

            retObj = Obj.writeValueAsString(getSegmentsResp);

        } catch (JsonProcessingException ex) {
            logger.error(AdminLibrary.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);

            getSegmentsResp.setResponseCode(1);
            getSegmentsResp.setResponseMessage("Error");
            getSegmentsResp.setMessageId(suppliedGetSegmentsData.getMessageId());
            getSegmentsResp.setResponseBody(null);
            retObj = Obj.writeValueAsString(getSegmentsResp);
        }
        return retObj;
    }
}
