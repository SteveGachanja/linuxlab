package com.coop.mwalletapi.customer.accountsummary.dao;
import com.coop.mwalletapi.customer.accountsummary.dao.model.AccountDataDbDao;
import com.coop.mwalletapi.customer.accountsummary.dao.model.AccountDataDbResp;
import com.coop.mwalletapi.customer.accountsummary.dao.model.GetCustomerAllAccountsDbData;
import com.coop.mwalletapi.customer.accountsummary.dao.model.GetCustomerAllAccountsDbResp;
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
 * @author pkingongo
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
    
    public AccountDataDbResp getMClientData(String documentNo,int entity) {
        AccountDataDbResp accountDataDbResp = new AccountDataDbResp();
        List<AccountDataDbDao> retStr = Collections.emptyList();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("GET_CUSTOMER_ACCOUNTS")
            .withCatalogName("M_MWALLET_ACC_SUMMARY")
            .returningResultSet("O_ACCOUNT_DETAILS", BeanPropertyRowMapper.newInstance(AccountDataDbDao.class))
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                   
                    new SqlParameter("I_DOCUMENT_NO", Types.VARCHAR),
                    new SqlParameter("I_ENTITY_ID", Types.VARCHAR),
                    new SqlOutParameter("O_ACCOUNT_DETAILS", Types.REF_CURSOR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            
            inParamMap.put("I_DOCUMENT_NO", documentNo);
            inParamMap.put("I_ENTITY_ID", entity);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                retStr = Collections.emptyList();
                
                accountDataDbResp.setResponseCode(1);
                accountDataDbResp.setResponseMessage("No Data");
                accountDataDbResp.setAccountData(retStr);
            } else {
                retStr = (List) out.get("O_ACCOUNT_DETAILS");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                accountDataDbResp.setResponseCode(retMessageCodeStr);
                accountDataDbResp.setResponseMessage(retMessageDescriptionStr);
                accountDataDbResp.setAccountData(retStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return accountDataDbResp;
    }           
            
    public GetCustomerAllAccountsDbResp getCustomerAllAccountsData(
            int entityId, String documentNo, String mobileNo, String customerNo
    ) {
        GetCustomerAllAccountsDbResp customerDataDbResp = new GetCustomerAllAccountsDbResp();
        List<GetCustomerAllAccountsDbData> retStr = Collections.emptyList();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("GET_CUSTOMER_ALL_ACCOUNTS")
            .withCatalogName("M_MWALLET_ACC_SUMMARY")
            .returningResultSet("O_CUSTOMER_ACCOUNTS_DETAILS", BeanPropertyRowMapper.newInstance(GetCustomerAllAccountsDbData.class))
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                    new SqlParameter("I_DOCUMENT_NO", Types.VARCHAR),
                    new SqlParameter("I_MOBILE_NO", Types.VARCHAR),
                    new SqlParameter("I_CUSTOMER_NO", Types.VARCHAR),
                    new SqlOutParameter("O_CUSTOMER_ACCOUNTS_DETAILS", Types.REF_CURSOR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ENTITY_ID", entityId);
            inParamMap.put("I_DOCUMENT_NO", documentNo);
            inParamMap.put("I_MOBILE_NO", mobileNo);
            inParamMap.put("I_CUSTOMER_NO", customerNo);
            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                retStr = Collections.emptyList();
                
                customerDataDbResp.setResponseCode(1);
                customerDataDbResp.setResponseMessage("No Data");
                customerDataDbResp.setAccountsData(retStr);
            } else {
                retStr = (List) out.get("O_CUSTOMER_ACCOUNTS_DETAILS");
                
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                customerDataDbResp.setResponseCode(retMessageCodeStr);
                customerDataDbResp.setResponseMessage(retMessageDescriptionStr);
                customerDataDbResp.setAccountsData(retStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return customerDataDbResp;
    }
}
