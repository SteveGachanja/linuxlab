package com.coop.mwalletapi.mwalletupdateaccountflaglibrary.dao;

import com.coop.mwalletapi.commons.ResponseHeader;
import com.coop.mwalletapi.mwalletupdateaccountflaglibrary.dao.model.UpdateAccountFlagRespData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fosano
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

    public UpdateAccountFlagRespData getUpdateAccountFlagDbResponse(String entityID,
                                                                    String actionType,
                                                                    String accountNumber,
                                                                    String reason,
                                                                    String channelID,
                                                                    //Date actionDate,
                                                                    String status,
                                                                    String makerID,
                                                                    Date makerDate,
                                                                    String approverID,
                                                                    Date approverDate,
                                                                    ResponseHeader header) {
        UpdateAccountFlagRespData updateAccountFlagDbResp = new UpdateAccountFlagRespData();
        //List<BlockAccountDbResponse> resStr = Collections.emptyList();
        int resMessageCodeStr = 0;
        String resMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("UPDATE_ACCOUNT_FLAG")
                    .withCatalogName("M_WALLET_ADMIN_PACKAGE")
                    //.returningResultSet("O_ACCOUNT_DETAILS", BeanPropertyRowMapper.newInstance(BlockAccountDbResponse.class))
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("I_ENTITY_ID", Types.VARCHAR),
                            new SqlParameter("I_CHANNEL_ID", Types.VARCHAR),
                            new SqlParameter("I_ACTIONTYPE", Types.VARCHAR),
                            new SqlParameter("I_ACCOUNT_ALIAS", Types.VARCHAR),
                            new SqlParameter("I_REASON", Types.VARCHAR),
                            new SqlParameter("I_MAKER_ID", Types.VARCHAR),
                            new SqlParameter("I_MAKER_DATE", Types.DATE),
                            new SqlParameter("I_APPROVER_ID", Types.VARCHAR),
                            new SqlParameter("I_APPROVER_DATE", Types.DATE),
                            new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                            new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
                    );

            Map<String, Object> inParamMap = new HashMap<String, Object>();

            inParamMap.put("I_ENTITY_ID", entityID);
            inParamMap.put("I_CHANNEL_ID", channelID);
            inParamMap.put("I_ACTIONTYPE", actionType);
            inParamMap.put("I_ACCOUNT_ALIAS", accountNumber);
            inParamMap.put("I_REASON", reason);
            inParamMap.put("I_MAKER_ID", makerID);
            inParamMap.put("I_MAKER_DATE", makerDate);
            inParamMap.put("I_APPROVER_ID", approverID);
            inParamMap.put("I_APPROVER_DATE", approverDate);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                //resStr = Collections.emptyList();
                header.setResponseCode(1);
                header.setResponseMessage("Error");
                updateAccountFlagDbResp.setStatusCode(1);
                updateAccountFlagDbResp.setStatusDescription("No Valid Response");
            } else {
                resMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                resMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");

                //Check for Error Output
                if(resMessageCodeStr == 0) {
                    header.setResponseCode(0);
                    header.setResponseMessage(resMessageDescriptionStr);
                }
                else {
                    header.setResponseCode(-1);
                    header.setResponseMessage("Error");
                }
                updateAccountFlagDbResp.setStatusCode(resMessageCodeStr);
                updateAccountFlagDbResp.setStatusDescription(resMessageDescriptionStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return updateAccountFlagDbResp;
    }
}
