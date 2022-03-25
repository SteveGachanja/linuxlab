package com.coop.mwalletapi.customerslibrary.dao;

import com.coop.mwalletapi.customerslibrary.dao.model.CheckEmailDbResp;
import com.coop.mwalletapi.customerslibrary.dao.model.CreateAdditionalCustomerWalletDbResp;
import com.coop.mwalletapi.customerslibrary.dao.model.CreateCustomerDbResp;
import com.coop.mwalletapi.customerslibrary.dao.model.EditCustomerDbResp;
import com.coop.mwalletapi.customerslibrary.dao.model.GetCustomerDataDb;
import com.coop.mwalletapi.customerslibrary.dao.model.GetCustomerDataDbResp;
import com.coop.mwalletapi.customerslibrary.entities.customeredit.SuppliedCustomerEditData;
import java.sql.Types;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

/**
 * @author okahia
 */

public class Db {
    private static final Logger logger = LogManager.getLogger(Db.class.getName());
    
    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall simpleJdbcCallRefCursor;
    private DataSource ds;
    
    @Autowired
    public Db(DataSource dsPassed) {
       this.jdbcTemplate = new JdbcTemplate(dsPassed);
       this.ds = dsPassed;
    }
    
    public GetCustomerDataDbResp getMClientData(
            int entityId, String documentType, String documentNo, String mobileNo, String customerNo
    ) {
        GetCustomerDataDbResp customerDataDbResp = new GetCustomerDataDbResp();
        List<GetCustomerDataDb> retStr = Collections.emptyList();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("GET_CUSTOMER_DETAILS")
            .withCatalogName("M_WALLET_PACKAGE")
            .returningResultSet("O_CUSTOMER_DETAILS", BeanPropertyRowMapper.newInstance(GetCustomerDataDb.class))
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                    new SqlParameter("I_DOCUMENT_TYPE", Types.VARCHAR),
                    new SqlParameter("I_DOCUMENT_NO", Types.VARCHAR),
                    new SqlParameter("I_MOBILE_NO", Types.VARCHAR),
                    new SqlParameter("I_CUSTOMER_NO", Types.VARCHAR),
                    new SqlOutParameter("O_CUSTOMER_DETAILS", Types.REF_CURSOR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ENTITY_ID", entityId);
            inParamMap.put("I_DOCUMENT_TYPE", documentType);
            inParamMap.put("I_DOCUMENT_NO", documentNo);
            inParamMap.put("I_MOBILE_NO", mobileNo);
            inParamMap.put("I_CUSTOMER_NO", customerNo);
            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                retStr = Collections.emptyList();
                
                customerDataDbResp.setResponseCode(1);
                customerDataDbResp.setResponseMessage("No Data");
                customerDataDbResp.setMCustData(retStr);
            } else {
                retStr = (List) out.get("O_CUSTOMER_DETAILS");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                customerDataDbResp.setResponseCode(retMessageCodeStr);
                customerDataDbResp.setResponseMessage(retMessageDescriptionStr);
                customerDataDbResp.setMCustData(retStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return customerDataDbResp;
    }
    
    public CreateCustomerDbResp createMClientData(int entityId,
            String productCode, String documentType, String documentNumber,
            String mobileNumber, String emailAddress, String title,
            String firstName, String middleName, String lastName,
            String dateOfBirth, String gender, String nationalityId, String branchCode,
            String accountOfficerCode, String salesOfficerCode,
            int segmentId, int status, String makerUserId,
            String makerDateTime, String approverUserId, String approverDateTime
    ) {
        CreateCustomerDbResp customerDataDbResp = new CreateCustomerDbResp();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        String customerNumber;
        String accountId;
        String accountNumber;
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("CREATE_CUSTOMER_WALLET")
            .withCatalogName("M_WALLET_PACKAGE")
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                    new SqlParameter("I_PRODUCT_CODE", Types.VARCHAR),
                    new SqlParameter("I_DOCUMENT_TYPE", Types.VARCHAR),
                    new SqlParameter("I_DOCUMENT_NO", Types.VARCHAR),
                    new SqlParameter("I_MOBILE_NO", Types.VARCHAR),
                    new SqlParameter("I_EMAIL_ADDRESS", Types.VARCHAR),
                    new SqlParameter("I_TITLE", Types.VARCHAR),
                    new SqlParameter("I_FIRST_NAME", Types.VARCHAR),
                    new SqlParameter("I_LAST_NAME", Types.VARCHAR),
                    new SqlParameter("I_MIDDLE_NAME", Types.VARCHAR),
                    new SqlParameter("I_DOB", Types.VARCHAR),
                    new SqlParameter("I_GENDER", Types.VARCHAR),
                    new SqlParameter("I_NATIONALITY_ID", Types.VARCHAR),
                    new SqlParameter("I_BRANCH_CODE", Types.VARCHAR),
                    new SqlParameter("I_ACC_OFFICER_CODE", Types.VARCHAR),
                    new SqlParameter("I_SALE_OFFICER_CODE", Types.VARCHAR),
                    new SqlParameter("I_SEGMENT_ID", Types.NUMERIC),
                    new SqlParameter("I_STATUS", Types.NUMERIC),
                    new SqlParameter("I_STATUS_CHANGE_DATE", Types.TIMESTAMP),
                    new SqlParameter("I_MAKER_ID", Types.VARCHAR),
                    new SqlParameter("I_MAKER_DATE", Types.TIMESTAMP),
                    new SqlParameter("I_APPROVER_ID", Types.VARCHAR),
                    new SqlParameter("I_APPROVER_DATE", Types.TIMESTAMP),
                    new SqlParameter("I_MODIFIED_BY", Types.VARCHAR),
                    new SqlParameter("I_DATE_MODIFIED", Types.TIMESTAMP),
                    new SqlOutParameter("O_CUSTOMER_NUMBER", Types.VARCHAR),
                    new SqlOutParameter("O_ACCOUNT_ID", Types.VARCHAR),
                    new SqlOutParameter("O_ACCOUNT_NUMBER", Types.VARCHAR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ENTITY_ID", entityId);
            inParamMap.put("I_PRODUCT_CODE", productCode);
            inParamMap.put("I_DOCUMENT_TYPE", documentType);
            inParamMap.put("I_DOCUMENT_NO", documentNumber);
            inParamMap.put("I_MOBILE_NO", mobileNumber);
            inParamMap.put("I_EMAIL_ADDRESS", emailAddress);
            inParamMap.put("I_TITLE", title);
            inParamMap.put("I_FIRST_NAME", firstName);
            inParamMap.put("I_LAST_NAME", lastName);
            inParamMap.put("I_MIDDLE_NAME", middleName);
            inParamMap.put("I_DOB", dateOfBirth);
            inParamMap.put("I_GENDER", gender);
            inParamMap.put("I_NATIONALITY_ID", nationalityId);
            inParamMap.put("I_BRANCH_CODE", branchCode);
            inParamMap.put("I_ACC_OFFICER_CODE", accountOfficerCode);
            inParamMap.put("I_SALE_OFFICER_CODE", salesOfficerCode);
            inParamMap.put("I_SEGMENT_ID", segmentId);
            inParamMap.put("I_STATUS", status); //--
            inParamMap.put("I_STATUS_CHANGE_DATE", makerDateTime);
            inParamMap.put("I_MAKER_ID", makerUserId);
            inParamMap.put("I_MAKER_DATE", makerDateTime);
            inParamMap.put("I_APPROVER_ID", approverUserId);
            inParamMap.put("I_APPROVER_DATE", approverDateTime);
            inParamMap.put("I_MODIFIED_BY", "");
            inParamMap.put("I_DATE_MODIFIED", makerDateTime);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                customerDataDbResp.setResponseCode(1);
                customerDataDbResp.setResponseMessage("No Data");

            } else {
                customerNumber = (String) out.get("O_CUSTOMER_NUMBER");
                accountId = (String) out.get("O_ACCOUNT_ID");
                accountNumber = (String) out.get("O_ACCOUNT_NUMBER");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                customerDataDbResp.setResponseCode(retMessageCodeStr);
                customerDataDbResp.setResponseMessage(retMessageDescriptionStr);
                customerDataDbResp.setAccountNumber(accountNumber);
                customerDataDbResp.setAccountId(accountId);
                customerDataDbResp.setCustomerNumber(customerNumber);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return customerDataDbResp;
    }
    
    
    public CreateAdditionalCustomerWalletDbResp createAdditionalCustomerWallet(int entityId,
            String productCode, String documentNumber,
            String makerUserId, String makerDateTime, 
            String approverUserId, String approverDateTime, String branchCode
    ) {
        CreateAdditionalCustomerWalletDbResp customerDataDbResp = new CreateAdditionalCustomerWalletDbResp();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        String customerNumber;
        String accountId;
        String accountNumber;
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("CREATE_ADDITIONAL_WALLET")
            .withCatalogName("M_WALLET_PACKAGE")
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                    new SqlParameter("I_PRODUCT_CODE", Types.VARCHAR),
                    new SqlParameter("I_DOCUMENT_NO", Types.VARCHAR),
                    new SqlParameter("I_MAKER_ID", Types.VARCHAR),
                    new SqlParameter("I_MAKER_DATE", Types.TIMESTAMP),
                    new SqlParameter("I_APPROVER_ID", Types.VARCHAR),
                    new SqlParameter("I_APPROVER_DATE", Types.TIMESTAMP),
                    new SqlParameter("I_BRANCH_CODE", Types.VARCHAR),
                    new SqlOutParameter("O_CUSTOMER_NUMBER", Types.VARCHAR),
                    new SqlOutParameter("O_ACCOUNT_ID", Types.VARCHAR),
                    new SqlOutParameter("O_ACCOUNT_NUMBER", Types.VARCHAR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ENTITY_ID", entityId);
            inParamMap.put("I_PRODUCT_CODE", productCode);
            inParamMap.put("I_DOCUMENT_NO", documentNumber);
            inParamMap.put("I_STATUS_CHANGE_DATE", makerDateTime);
            inParamMap.put("I_MAKER_ID", makerUserId);
            inParamMap.put("I_MAKER_DATE", makerDateTime);
            inParamMap.put("I_APPROVER_ID", approverUserId);
            inParamMap.put("I_APPROVER_DATE", approverDateTime);
            inParamMap.put("I_BRANCH_CODE", branchCode);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                customerDataDbResp.setResponseCode(1);
                customerDataDbResp.setResponseMessage("No Data");

            } else {
                customerNumber = (String) out.get("O_CUSTOMER_NUMBER");
                accountId = (String) out.get("O_ACCOUNT_ID");
                accountNumber = (String) out.get("O_ACCOUNT_NUMBER");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                customerDataDbResp.setResponseCode(retMessageCodeStr);
                customerDataDbResp.setResponseMessage(retMessageDescriptionStr);
                customerDataDbResp.setAccountNumber(accountNumber);
                customerDataDbResp.setAccountId(accountId);
                customerDataDbResp.setCustomerNumber(customerNumber);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return customerDataDbResp;
    }
    
            
    public EditCustomerDbResp editCustomer(
            int apiUser, String dob, SuppliedCustomerEditData editCustReq
    ) {
        EditCustomerDbResp editCustDbResp = new EditCustomerDbResp();
        
        try {
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("EDIT_CUSTOMER_WALLET")
            .withCatalogName("M_WALLET_PACKAGE")
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                    new SqlParameter("I_CUSTOMER_ID", Types.NUMERIC),
                    new SqlParameter("I_CUSTOMER_NUMBER", Types.VARCHAR),
                    new SqlParameter("I_DOCUMENT_TYPE", Types.VARCHAR),
                    new SqlParameter("I_DOCUMENT_NO", Types.VARCHAR),
                    new SqlParameter("I_MOBILE_NO", Types.VARCHAR),
                    new SqlParameter("I_EMAIL_ADDRESS", Types.VARCHAR),
                    new SqlParameter("I_TITLE", Types.VARCHAR),
                    new SqlParameter("I_FIRST_NAME", Types.VARCHAR),
                    new SqlParameter("I_MIDDLE_NAME", Types.VARCHAR),
                    new SqlParameter("I_LAST_NAME", Types.VARCHAR),
                    new SqlParameter("I_DOB", Types.VARCHAR),
                    new SqlParameter("I_GENDER", Types.VARCHAR),
                    new SqlParameter("I_BRANCH_CODE", Types.VARCHAR),
                    new SqlParameter("I_ACC_OFFICER_CODE", Types.VARCHAR),
                    new SqlParameter("I_SALE_OFFICER_CODE", Types.VARCHAR),
                    new SqlParameter("I_SEGMENT_ID", Types.VARCHAR),
                    new SqlParameter("I_STATUS", Types.VARCHAR),
                    new SqlParameter("I_MODIFIED_BY", Types.VARCHAR),
                    new SqlParameter("I_NATIONALITY_ID", Types.VARCHAR),
                    new SqlOutParameter("O_RESPONSE_DESCRIPTION", Types.VARCHAR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            
            inParamMap.put("I_ENTITY_ID", apiUser);
            inParamMap.put("I_CUSTOMER_ID", editCustReq.getRequestBody().getCustomerId());
            inParamMap.put("I_CUSTOMER_NUMBER", editCustReq.getRequestBody().getCustomerNumber());
            inParamMap.put("I_DOCUMENT_TYPE", editCustReq.getRequestBody().getDocumentType());
            inParamMap.put("I_DOCUMENT_NO", editCustReq.getRequestBody().getDocumentNumber());
            inParamMap.put("I_MOBILE_NO", editCustReq.getRequestBody().getMobileNumber());
            inParamMap.put("I_EMAIL_ADDRESS", editCustReq.getRequestBody().getEmailAddress());
            inParamMap.put("I_TITLE", editCustReq.getRequestBody().getTitle());
            inParamMap.put("I_FIRST_NAME", editCustReq.getRequestBody().getFirstName());
            inParamMap.put("I_MIDDLE_NAME", editCustReq.getRequestBody().getMiddleName());
            inParamMap.put("I_LAST_NAME", editCustReq.getRequestBody().getLastName());
            inParamMap.put("I_DOB", dob);
            inParamMap.put("I_GENDER", editCustReq.getRequestBody().getGender());
            inParamMap.put("I_BRANCH_CODE", editCustReq.getRequestBody().getBranchCode());
            inParamMap.put("I_ACC_OFFICER_CODE", editCustReq.getRequestBody().getAccountOfficerCode());
            inParamMap.put("I_SALE_OFFICER_CODE", editCustReq.getRequestBody().getSalesOfficerCode());
            inParamMap.put("I_SEGMENT_ID", editCustReq.getRequestBody().getSegmentId());
            inParamMap.put("I_STATUS", editCustReq.getRequestBody().getStatus());
            inParamMap.put("I_MODIFIED_BY", editCustReq.getRequestBody().getModifiedBy());
            inParamMap.put("I_NATIONALITY_ID", editCustReq.getRequestBody().getNationality());
            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);
            
            String responseDescription = (String) out.get("O_RESPONSE_DESCRIPTION");
            int messageCode = (int) out.get("O_MESSAGE_CODE");
            String messageDescription = (String) out.get("O_MESSAGE_DESCRIPTION");
            
            editCustDbResp.setMessageId(editCustReq.getMessageId());
            editCustDbResp.setResponseCode(messageCode);
            editCustDbResp.setResponseMessage(messageDescription);
            editCustDbResp.setStatusDescription(responseDescription);
            
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
            
            editCustDbResp.setMessageId(editCustReq.getMessageId());
            editCustDbResp.setResponseCode(1);
            editCustDbResp.setResponseMessage("Error Editing Customer information: See application logs");
            editCustDbResp.setStatusDescription("");
        }
        
        return editCustDbResp;
    }
    
    
    
    public CheckEmailDbResp checkEmailAddress(
            int entityId, double customerId, String emailAddress
    ) {
        CheckEmailDbResp checkEmailDbResp = new CheckEmailDbResp();
        List<GetCustomerDataDb> retStr = Collections.emptyList();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("CHECK_IF_EMAIL_EXISTS")
            .withCatalogName("M_WALLET_PACKAGE")
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_CUSTOMER_ID", Types.NUMERIC),
                    new SqlParameter("I_EMAIL_ADDRESS", Types.VARCHAR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_CUSTOMER_ID", customerId);
            inParamMap.put("I_EMAIL_ADDRESS", emailAddress);
            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                retStr = Collections.emptyList();
                
                checkEmailDbResp.setStatusCode("1");
                checkEmailDbResp.setStatusMessage("No Data");
            } else {
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                checkEmailDbResp.setStatusCode(Integer.toString(retMessageCodeStr));
                checkEmailDbResp.setStatusMessage(retMessageDescriptionStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return checkEmailDbResp;
    }
            
}
