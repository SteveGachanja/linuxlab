package com.coop.mwalletapi.adminlibrary.dao;

import com.coop.mwalletapi.adminlibrary.dao.model.CreateApiUserDbResp;
import com.coop.mwalletapi.adminlibrary.dao.model.GetApiUserDataDb;
import com.coop.mwalletapi.adminlibrary.dao.model.GetApiUserDataDbResp;
import com.coop.mwalletapi.adminlibrary.dao.model.GetNationalitiesDataDb;
import com.coop.mwalletapi.adminlibrary.dao.model.GetNationalitiesDataDbResp;
import com.coop.mwalletapi.adminlibrary.dao.model.GetProductsDataDb;
import com.coop.mwalletapi.adminlibrary.dao.model.GetProductsDataDbResp;
import com.coop.mwalletapi.adminlibrary.dao.model.GetSegmentsDataDb;
import com.coop.mwalletapi.adminlibrary.dao.model.GetSegmentsDataDbResp;
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
        
    public CreateApiUserDbResp createApiUserInDb(
            int entityId,
            String userName, 
            String password,
            String apiUser
    ) {
        CreateApiUserDbResp customerDataDbResp = new CreateApiUserDbResp();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        String oUserId;
        String oUserName;
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("CREATE_API_USER_WALLET")
            .withCatalogName("M_WALLET_ADMIN_PACKAGE")
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                    new SqlParameter("I_USER_NAME", Types.VARCHAR),
                    new SqlParameter("I_PASSWORD", Types.VARCHAR),
                    new SqlParameter("I_API_USER", Types.VARCHAR),
                    new SqlOutParameter("O_USER_ID", Types.VARCHAR),
                    new SqlOutParameter("O_USER_NAME", Types.VARCHAR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ENTITY_ID", entityId);
            inParamMap.put("I_USER_NAME", userName);
            inParamMap.put("I_PASSWORD", password);
            inParamMap.put("I_API_USER", apiUser);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                customerDataDbResp.setResponseCode(1);
                customerDataDbResp.setResponseMessage("No Data");

            } else {
                oUserId = (String) out.get("O_USER_ID");
                oUserName = (String) out.get("O_USER_NAME");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                customerDataDbResp.setResponseCode(retMessageCodeStr);
                customerDataDbResp.setResponseMessage(retMessageDescriptionStr);
                customerDataDbResp.setApiUserId(oUserId);
                customerDataDbResp.setApiUserName(oUserName);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return customerDataDbResp;
    }
    
    public GetApiUserDataDbResp getApiUsersFromDb() {
        GetApiUserDataDbResp apiUserDataDbResp = new GetApiUserDataDbResp();
        List<GetApiUserDataDb> retStr = Collections.emptyList();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("GET_ALL_API_USER_WALLET")
            .withCatalogName("M_WALLET_ADMIN_PACKAGE")
            .returningResultSet("O_API_USERS", BeanPropertyRowMapper.newInstance(GetApiUserDataDb.class))
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlOutParameter("O_API_USERS", Types.REF_CURSOR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                retStr = Collections.emptyList();
                
                apiUserDataDbResp.setResponseCode(1);
                apiUserDataDbResp.setResponseMessage("No Data");
                apiUserDataDbResp.setApiUsers(retStr);
            } else {
                retStr = (List) out.get("O_API_USERS");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                apiUserDataDbResp.setResponseCode(retMessageCodeStr);
                apiUserDataDbResp.setResponseMessage(retMessageDescriptionStr);
                apiUserDataDbResp.setApiUsers(retStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return apiUserDataDbResp;
    }
    
    
    public GetNationalitiesDataDbResp getNationalitiesFromDb(String searchByName, String searchByCode) {
        GetNationalitiesDataDbResp nationalitiesDbResp = new GetNationalitiesDataDbResp();
        List<GetNationalitiesDataDb> retStr = Collections.emptyList();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("GET_NATIONALITIES")
            .withCatalogName("M_WALLET_ADMIN_PACKAGE")
            .returningResultSet("O_NATIONALITIES", BeanPropertyRowMapper.newInstance(GetNationalitiesDataDb.class))
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_SEARCH_BY_NAME", Types.VARCHAR),
                    new SqlParameter("I_SEARCH_BY_CODE", Types.VARCHAR),
                    new SqlOutParameter("O_NATIONALITIES", Types.REF_CURSOR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_SEARCH_BY_NAME", searchByName);
            inParamMap.put("I_SEARCH_BY_CODE", searchByCode);
            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                retStr = Collections.emptyList();
                
                nationalitiesDbResp.setResponseCode(1);
                nationalitiesDbResp.setResponseMessage("No Data");
                nationalitiesDbResp.setNationalities(retStr);
            } else {
                retStr = (List) out.get("O_NATIONALITIES");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                nationalitiesDbResp.setResponseCode(retMessageCodeStr);
                nationalitiesDbResp.setResponseMessage(retMessageDescriptionStr);
                nationalitiesDbResp.setNationalities(retStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return nationalitiesDbResp;
    }
    
    
    public GetProductsDataDbResp getProductsFromDb(int entityId, String productCode) {
        GetProductsDataDbResp productsDbResp = new GetProductsDataDbResp();
        List<GetProductsDataDb> retStr = Collections.emptyList();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("GET_PRODUCTS")
            .withCatalogName("M_WALLET_ADMIN_PACKAGE")
            .returningResultSet("O_PRODUCTS", BeanPropertyRowMapper.newInstance(GetProductsDataDb.class))
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.VARCHAR),
                    new SqlParameter("I_PRODUCT_CODE", Types.VARCHAR),
                    new SqlOutParameter("O_PRODUCTS", Types.REF_CURSOR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ENTITY_ID", entityId);
            inParamMap.put("I_PRODUCT_CODE", productCode);
            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                retStr = Collections.emptyList();
                
                productsDbResp.setResponseCode(1);
                productsDbResp.setResponseMessage("No Data");
                productsDbResp.setProducts(retStr);
            } else {
                retStr = (List) out.get("O_PRODUCTS");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                productsDbResp.setResponseCode(retMessageCodeStr);
                productsDbResp.setResponseMessage(retMessageDescriptionStr);
                productsDbResp.setProducts(retStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return productsDbResp;
    }
    
    public GetSegmentsDataDbResp getSegmentsFromDb(int entityId, String segmentCode) {
        GetSegmentsDataDbResp SegmentsDbResp = new GetSegmentsDataDbResp();
        List<GetSegmentsDataDb> retStr = Collections.emptyList();
        int retMessageCodeStr = 0;
        String retMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("GET_SEGMENTS")
            .withCatalogName("M_WALLET_ADMIN_PACKAGE")
            .returningResultSet("O_SEGMENTS", BeanPropertyRowMapper.newInstance(GetSegmentsDataDb.class))
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.VARCHAR),
                    new SqlParameter("I_SEGMENT_CODE", Types.VARCHAR),
                    new SqlOutParameter("O_SEGMENTS", Types.REF_CURSOR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ENTITY_ID", entityId);
            inParamMap.put("I_SEGMENT_CODE", segmentCode);
            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                retStr = Collections.emptyList();
                
                SegmentsDbResp.setResponseCode(1);
                SegmentsDbResp.setResponseMessage("No Data");
                SegmentsDbResp.setSegments(retStr);
            } else {
                retStr = (List) out.get("O_SEGMENTS");
                retMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                retMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");
                
                SegmentsDbResp.setResponseCode(retMessageCodeStr);
                SegmentsDbResp.setResponseMessage(retMessageDescriptionStr);
                SegmentsDbResp.setSegments(retStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return SegmentsDbResp;
    }
    
}
