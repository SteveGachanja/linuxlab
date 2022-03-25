package com.coop.mwalletapi.accountbalancelibrary.dao;

import com.coop.mwalletapi.accountbalancelibrary.dao.model.AccountBalanceEnquiryDataDbResp;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

/**
 * @author cndirangu
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

    public AccountBalanceEnquiryDataDbResp getMClientData(String accountAlias, int entityId) {
        try {
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("ACCOUNT_BALANCE_ENQUIRYV1")
                    .withCatalogName("M_WALLET_ACC_BAL_AND_MINISTMT")
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("IO_ACCOUNT_ALIAS", Types.VARCHAR),
                            new SqlParameter("IO_ENTITY_ID", Types.VARCHAR),
                            new SqlOutParameter("O_ACCOUNT_NUMBER", Types.VARCHAR),
                            new SqlOutParameter("O_ACCOUNT_NAME", Types.VARCHAR),
                            new SqlOutParameter("O_BOOKEDBALANCE", Types.NUMERIC),
                            new SqlOutParameter("O_CLEAREDBALANCE", Types.NUMERIC),
                            new SqlOutParameter("O_BLOCKEDBALANCE", Types.NUMERIC),
                            new SqlOutParameter("O_AVAILABLEBALANCE", Types.NUMERIC),
                            new SqlOutParameter("O_MAX_BALANCE", Types.NUMERIC),
                            new SqlOutParameter("O_ARREARSAMOUNT", Types.NUMERIC),
                            new SqlOutParameter("O_BRANCHSORTCODE", Types.VARCHAR),
                            new SqlOutParameter("O_BRANCHNAME", Types.VARCHAR),
                            new SqlOutParameter("O_PRODUCTCODE", Types.VARCHAR),
                            new SqlOutParameter("O_PRODUCTID", Types.VARCHAR),
                            new SqlOutParameter("O_PRODUCTNAME", Types.VARCHAR),
                            new SqlOutParameter("O_ODLIMIT", Types.NUMERIC),
                            new SqlOutParameter("O_CREDITLIMIT", Types.NUMERIC),
                            new SqlOutParameter("O_CURRENCY", Types.VARCHAR),
                            new SqlOutParameter("O_RESPONSECODE", Types.INTEGER),
                            new SqlOutParameter("O_RESPONSEMESSAGE", Types.VARCHAR)
                    );

            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("IO_ACCOUNT_ALIAS", accountAlias);
            inParamMap.put("IO_ENTITY_ID", entityId);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            AccountBalanceEnquiryDataDbResp accountDataDbResp = new AccountBalanceEnquiryDataDbResp();
            if (out == null) {
                accountDataDbResp.setResponseCode(1);
                accountDataDbResp.setResponseMessage("No Data");

            } else {
                accountDataDbResp.setResponseCode((int) out.get("O_RESPONSECODE"));
                accountDataDbResp.setResponseMessage((String) out.get("O_RESPONSEMESSAGE"));
                if (accountDataDbResp.getResponseCode() == 0) {
                    accountDataDbResp.setAccountAlias(accountAlias);
                    accountDataDbResp.setEntityId(""+entityId);
                    accountDataDbResp.setAccountNumber((String) out.get("O_ACCOUNT_NUMBER"));
                    accountDataDbResp.setAccountName((String) out.get("O_ACCOUNT_NAME"));
                    accountDataDbResp.setBlockedBalance(((BigDecimal) out.getOrDefault("O_BOOKEDBALANCE","0")).doubleValue());
                    accountDataDbResp.setClearedBalance(((BigDecimal) out.getOrDefault("O_CLEAREDBALANCE","0")).doubleValue());
                    accountDataDbResp.setBlockedBalance(((BigDecimal) out.getOrDefault("O_BLOCKEDBALANCE","0")).doubleValue());
                    accountDataDbResp.setAvailableBalance(((BigDecimal) out.getOrDefault("O_AVAILABLEBALANCE","0")).doubleValue());
                    accountDataDbResp.setMaximumBalance(((BigDecimal) out.getOrDefault("O_MAX_BALANCE","0")).doubleValue());
                    accountDataDbResp.setArrearsAmount(((BigDecimal) out.getOrDefault("O_ARREARSAMOUNT","0")).doubleValue());
                    accountDataDbResp.setBranchSortCode((String) out.get("O_BRANCHSORTCODE"));
                    accountDataDbResp.setBranchName((String) out.get("O_BRANCHNAME"));
                    accountDataDbResp.setProductCode((String) out.get("O_PRODUCTCODE"));
                    accountDataDbResp.setProductId((String) out.get("O_PRODUCTID"));
                    accountDataDbResp.setProductName((String) out.get("O_PRODUCTNAME"));
                    accountDataDbResp.setDebitLimit(((BigDecimal) out.getOrDefault("O_ODLIMIT","0")).doubleValue());
                    accountDataDbResp.setCreditLimit(((BigDecimal) out.getOrDefault("O_CREDITLIMIT","0")).doubleValue());
                    accountDataDbResp.setCurrency((String) out.get("O_CURRENCY"));
                }
            }
            return accountDataDbResp;
        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return null;
    }
}
