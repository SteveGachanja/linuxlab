package com.coop.mwalletapi.accountstatementlibrary.dao;

import com.coop.mwalletapi.accountstatementlibrary.entities.AccountStatement;
import com.coop.mwalletapi.accountstatementlibrary.dao.model.AccountStatementDataDbResponse;
import com.coop.mwalletapi.commons.ResponseHeader;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    public AccountStatementDataDbResponse getMClientData(String accountAlias, int entityId, String transactionId, int maxRows, Date startDate, Date endDate, ResponseHeader header) {
        AccountStatementDataDbResponse chargesDataDbResp = new AccountStatementDataDbResponse();
        try {
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("GET_ACCOUNT_STATEMENT_V2")
            .withCatalogName("M_WALLET_ACC_BAL_AND_MINISTMT")
            .returningResultSet("O_TRANSACTION_DETAILS", new TransactionsListRowMapper()  {})
            .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("I_ACCOUNT_ALIAS", Types.VARCHAR),
                            new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                            new SqlParameter("MAXIMUMROWS", Types.VARCHAR),
                            new SqlParameter("START_DATE", Types.DATE),
                            new SqlParameter("END_DATE", Types.DATE),
                            new SqlParameter("I_TRANSACTIONID", Types.VARCHAR),
                            new SqlOutParameter("O_TRANSACTION_DETAILS", Types.REF_CURSOR),
                            new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                            new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
                    );

            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ACCOUNT_ALIAS", accountAlias);
            inParamMap.put("I_ENTITY_ID", entityId);
            inParamMap.put("MAXIMUMROWS", maxRows);
            inParamMap.put("START_DATE", startDate);
            inParamMap.put("END_DATE", endDate);
            inParamMap.put("I_TRANSACTIONID", transactionId);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            List<AccountStatement> transactions = new ArrayList<>();

            if (out == null) {
                header.setResponseCode(1);
                header.setResponseMessage("No Data Found");

            } else {
                header.setResponseCode((int) out.get("O_MESSAGE_CODE"));
                header.setResponseMessage((String) out.get("O_MESSAGE_DESCRIPTION"));
                if (header.getResponseCode() == 0) {
                    transactions = (List) out.get("O_TRANSACTION_DETAILS");
                    
                    chargesDataDbResp.setTransactions(transactions);
                }
            }
        } catch (Exception ex) {
            header.setResponseCode(1);
            header.setResponseMessage(ex.getLocalizedMessage());
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return chargesDataDbResp;
    }
}
