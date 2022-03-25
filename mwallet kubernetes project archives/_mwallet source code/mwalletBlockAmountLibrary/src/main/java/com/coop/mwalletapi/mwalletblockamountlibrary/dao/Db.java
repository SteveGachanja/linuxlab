package com.coop.mwalletapi.mwalletblockamountlibrary.dao;

import com.coop.mwalletapi.commons.ResponseHeader;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.BlockAmountRespData;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.BlockedAmountDetails;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.BlockedAmountQueryRespData;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.UnblockAmountRespData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

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

    public BlockAmountRespData postBlockAmountDbResponse(String entityID,
                                                              String actionType,
                                                              String accountNumber,
                                                              Double blockAmount,
                                                              String reason,
                                                              String channelID,
                                                              //Date blockDate,
                                                              String externalReference,
                                                              //String active,
                                                              String status,
                                                              String makerID,
                                                              Date makerDate,
                                                              String approverID,
                                                              Date approverDate,
                                                              ResponseHeader header) {
        BlockAmountRespData blockAmountDbResp = new BlockAmountRespData();
        //List<BlockAccountDbResponse> resStr = Collections.emptyList();
        int resMessageCodeStr = 0;
        String resMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("BLOCK_AMOUNT")
                    .withCatalogName("M_WALLET_ADMIN_PACKAGE")
                    //.returningResultSet("O_ACCOUNT_DETAILS", BeanPropertyRowMapper.newInstance(BlockAccountDbResponse.class))
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            //new SqlParameter("I_REQUESTID", Types.VARCHAR),
                            new SqlParameter("I_ENTITY_ID", Types.VARCHAR),
                            new SqlParameter("I_ACTIONTYPE", Types.VARCHAR),
                            new SqlParameter("I_ACCOUNT_ALIAS", Types.VARCHAR),
                            new SqlParameter("I_BLOCKAMOUNT", Types.DECIMAL),
                            new SqlParameter("I_REASON", Types.VARCHAR),
                            new SqlParameter("I_CHANNELID", Types.VARCHAR),
                            //new SqlParameter("I_BLOCKDATE", Types.DATE),
                            new SqlParameter("I_EXTERNALREFERENCE", Types.VARCHAR),
                            //new SqlParameter("I_ACTIVE", Types.VARCHAR),
                            new SqlParameter("I_STATUS", Types.VARCHAR),
                            new SqlParameter("I_MAKER_ID", Types.VARCHAR),
                            new SqlParameter("I_MAKER_DATE", Types.DATE),
                            new SqlParameter("I_APPROVER_ID", Types.VARCHAR),
                            new SqlParameter("I_APPROVER_DATE", Types.DATE),
                            new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                            new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
                    );

            Map<String, Object> inParamMap = new HashMap<String, Object>();

            inParamMap.put("I_ENTITY_ID", entityID);
            inParamMap.put("I_ACTIONTYPE", actionType);
            inParamMap.put("I_ACCOUNT_ALIAS", accountNumber);
            inParamMap.put("I_BLOCKAMOUNT", blockAmount);
            inParamMap.put("I_REASON", reason);
            inParamMap.put("I_CHANNELID", channelID);
            //inParamMap.put("I_BLOCKDATE", blockDate);
            inParamMap.put("I_EXTERNALREFERENCE", externalReference);
            //inParamMap.put("I_ACTIVE", active);
            inParamMap.put("I_STATUS", status);
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
                blockAmountDbResp.setStatusCode(1);
                blockAmountDbResp.setStatusDescription("No Valid Response");
            } else {
                resMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                resMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");

                //Check for Error Output
                if(resMessageCodeStr == 0) {
                    header.setResponseCode(0);
                    header.setResponseMessage("Success");
                }
                else {
                    header.setResponseCode(-1);
                    header.setResponseMessage("Error");
                }
                blockAmountDbResp.setStatusCode(resMessageCodeStr);
                blockAmountDbResp.setStatusDescription(resMessageDescriptionStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return blockAmountDbResp;
    }

    public UnblockAmountRespData postUnblockAmountDbResponse(Long requestID,
                                                             String entityID,
                                                             String actionType,
                                                             String accountNumber,
                                                             Double blockAmount,
                                                             String reason,
                                                             String channelID,
                                                             //Date blockDate,
                                                             String externalReference,
                                                             //String active,
                                                             String status,
                                                             String makerID,
                                                             Date makerDate,
                                                             String approverID,
                                                             Date approverDate,
                                                             ResponseHeader header) {
        UnblockAmountRespData unblockAmountDbResp = new UnblockAmountRespData();
        //List<BlockAccountDbResponse> resStr = Collections.emptyList();
        int resMessageCodeStr = 0;
        String resMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("UNBLOCK_AMOUNT")
                    .withCatalogName("M_WALLET_ADMIN_PACKAGE")
                    //.returningResultSet("O_ACCOUNT_DETAILS", BeanPropertyRowMapper.newInstance(BlockAccountDbResponse.class))
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("I_REQUESTID", Types.VARCHAR),
                            new SqlParameter("I_ENTITY_ID", Types.VARCHAR),
                            new SqlParameter("I_ACTIONTYPE", Types.VARCHAR),
                            new SqlParameter("I_ACCOUNT_ALIAS", Types.VARCHAR),
                            new SqlParameter("I_BLOCKAMOUNT", Types.DECIMAL),
                            new SqlParameter("I_REASON", Types.VARCHAR),
                            new SqlParameter("I_CHANNELID", Types.VARCHAR),
                            //new SqlParameter("I_BLOCKDATE", Types.DATE),
                            new SqlParameter("I_EXTERNALREFERENCE", Types.VARCHAR),
                            //new SqlParameter("I_ACTIVE", Types.VARCHAR),
                            new SqlParameter("I_STATUS", Types.VARCHAR),
                            new SqlParameter("I_MAKER_ID", Types.VARCHAR),
                            new SqlParameter("I_MAKER_DATE", Types.DATE),
                            new SqlParameter("I_APPROVER_ID", Types.VARCHAR),
                            new SqlParameter("I_APPROVER_DATE", Types.DATE),
                            new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                            new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
                    );

            Map<String, Object> inParamMap = new HashMap<String, Object>();

            inParamMap.put("I_REQUESTID", requestID);
            inParamMap.put("I_ENTITY_ID", entityID);
            inParamMap.put("I_ACTIONTYPE", actionType);
            inParamMap.put("I_ACCOUNT_ALIAS", accountNumber);
            inParamMap.put("I_BLOCKAMOUNT", blockAmount);
            inParamMap.put("I_REASON", reason);
            inParamMap.put("I_CHANNELID", channelID);
            //inParamMap.put("I_BLOCKDATE", blockDate);
            inParamMap.put("I_EXTERNALREFERENCE", externalReference);
            //inParamMap.put("I_ACTIVE", active);
            inParamMap.put("I_STATUS", status);
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
                unblockAmountDbResp.setStatusCode(1);
                unblockAmountDbResp.setStatusDescription("No Valid Response");
            } else {
                resMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                resMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");

                //Check for Error Output
                if(resMessageCodeStr == 0) {
                    header.setResponseCode(0);
                    header.setResponseMessage("Success");
                }
                else {
                    header.setResponseCode(-1);
                    header.setResponseMessage("Error");
                }
                unblockAmountDbResp.setStatusCode(resMessageCodeStr);
                unblockAmountDbResp.setStatusDescription(resMessageDescriptionStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return unblockAmountDbResp;
    }

    public BlockedAmountQueryRespData getBlockedAmountQueryDbResponse(String actionType,
                                                                      String active,
                                                                      String accountNumber,
                                                                      ResponseHeader header) {
        BlockedAmountQueryRespData blockedAmountQueryDbResp = new BlockedAmountQueryRespData();
        //List<BlockAccountDbResponse> resStr = Collections.emptyList();
        int resMessageCodeStr = 0;
        String resMessageDescriptionStr = "";
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("BLOCKED_AMOUNT_QUERY")
                    .withCatalogName("M_WALLET_ADMIN_PACKAGE")
                    .returningResultSet("O_UNBLOCK_AMOUNTS_LIST", new RowMapper<BlockedAmountDetails>()  {
                        //@Override
                        //check if the ResultSet has records
                        public BlockedAmountDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                            BlockedAmountDetails details = new BlockedAmountDetails();

                            //if (!rs.wasNull()) {
                            //while (rs.next()) {
                                details.setRequestID(rs.getString("REQUESTID"));
                                details.setEntity_ID(rs.getString("ENTITY_ID"));
                                details.setActionType(rs.getString("ACTIONTYPE"));
                                details.setAccountNumber(rs.getString("ACCOUNTNUMBER"));
                                details.setAmount(rs.getString("AMOUNT"));
                                details.setReason(rs.getString("REASON"));
                                details.setChannelID(rs.getString("CHANNELID"));
                                details.setBlockDate(rs.getString("BLOCKDATE"));
                                details.setExternalReference(rs.getString("EXTERNALREFERENCE"));
                                details.setActive(rs.getString("ACTIVE"));
                                details.setBlockedBalanceBefore(rs.getString("BLOCKEDBALANCEBEFORE"));
                                details.setBlockedBalanceAfter(rs.getString("BLOCKEDBALANCEAFTER"));
                                details.setStatus(rs.getString("STATUS"));
                                details.setMakerID(rs.getString("MAKERID"));
                                details.setMakerDate(rs.getString("MAKERDATE"));
                                details.setApproverID(rs.getString("APPROVERID"));
                                details.setApproverDate(rs.getString("APPROVERDATE"));
                                details.setRecordDate(rs.getString("RECORDDATE"));
                            //}
                            return details;
                        }
                    })
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("I_ACTIONTYPE", Types.VARCHAR),
                            new SqlParameter("I_ACTIVE", Types.VARCHAR),
                            new SqlParameter("I_ACCOUNT_ALIAS", Types.VARCHAR),
                            new SqlOutParameter("O_UNBLOCK_AMOUNTS_LIST", Types.REF_CURSOR),
                            new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                            new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
                    );

            Map<String, Object> inParamMap = new HashMap<String, Object>();

            inParamMap.put("I_ACTIONTYPE", actionType);
            inParamMap.put("I_ACTIVE", active);
            inParamMap.put("I_ACCOUNT_ALIAS", accountNumber);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);
            List<BlockedAmountDetails> blockedAmountListing = new ArrayList<>();

            if (out == null) {
                //resStr = Collections.emptyList();
                header.setResponseCode(1);
                header.setResponseMessage("Error");
                blockedAmountQueryDbResp.setStatusCode(1);
                blockedAmountQueryDbResp.setStatusDescription("No Valid Response");
            } else {
                resMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                resMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");

                //Check for Error Output
                if(resMessageCodeStr == 0) {
                    header.setResponseCode(0);
                    header.setResponseMessage("Success");
                    blockedAmountListing = (List) out.get("O_UNBLOCK_AMOUNTS_LIST");
                    blockedAmountQueryDbResp.setBlockedAmountListing(blockedAmountListing);
                }
                else {
                    header.setResponseCode(-1);
                    header.setResponseMessage("Error");
                }
                blockedAmountQueryDbResp.setStatusCode(resMessageCodeStr);
                blockedAmountQueryDbResp.setStatusDescription(resMessageDescriptionStr);
            }
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
            header.setResponseCode(-1);
            header.setResponseMessage("Error");
            blockedAmountQueryDbResp.setStatusCode(-1);
            if (ex.getCause().toString().equalsIgnoreCase("java.sql.SQLException: No data read") || ex.getCause().toString().equalsIgnoreCase("java.sql.SQLException: Invalid column name")) {
                blockedAmountQueryDbResp.setStatusDescription("No matching Records for provided parameters.");
            } else{
                blockedAmountQueryDbResp.setStatusDescription("Error: " + ex.getCause());
            }
        }
        return blockedAmountQueryDbResp;
    }
}
