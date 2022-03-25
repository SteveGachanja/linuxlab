package com.coop.mwalletapi.transactions.dao;

import com.coop.mwalletapi.commons.ResponseHeader;
import com.coop.mwalletapi.transactions.dao.model.TransactionItems;
import com.coop.mwalletapi.transactions.dao.model.TransactionQueryDbResp;
import com.coop.mwalletapi.transactions.dao.model.TransactionQueryRequest;
import com.coop.mwalletapi.transactions.dao.model.TransactionQueryResponse;
import oracle.jdbc.OracleConnection;
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author okahia
 */
public class QueryTransactionDb {

    private static final Logger logger = LogManager.getLogger(QueryTransactionDb.class.getName());

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall simpleJdbcCallRefCursor;
    private DataSource ds;

    @Autowired
    public QueryTransactionDb(DataSource dsPassed) {
        this.jdbcTemplate = new JdbcTemplate(dsPassed);
        this.ds = dsPassed;
    }

    public TransactionQueryResponse queryTransaction(String transactionId, TransactionQueryRequest transactionQueryReq, ResponseHeader header) throws ClassNotFoundException, SQLException {
        TransactionQueryResponse transactionQueryResponse = new TransactionQueryResponse();
        TransactionQueryDbResp tranQueryDbResponse = new TransactionQueryDbResp();
        int resMessageCodeStr = 0;
        String resMessageDescriptionStr = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            OracleConnection connection = null;
            try (Connection hikariCon = jdbcTemplate.getDataSource().getConnection()) {
                if (hikariCon.isWrapperFor(OracleConnection.class)) {
                    connection = hikariCon.unwrap(OracleConnection.class);
                }
            }

            //construct the Object for Querying
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
                .withCatalogName("M_WALLET_POSTING")
                .withProcedureName("QUERY_TRANSACTION")
                .returningResultSet("O_TRANSACTION_DETAILS", new RowMapper<TransactionItems>()  {
                    //@Override
                    //check if the ResultSet has records
                    public TransactionItems mapRow(ResultSet rs, int rowNum) throws SQLException {
                        TransactionItems details = new TransactionItems();

                        details.setTransaction_ID(rs.getString("TRANSACTION_ID"));
                        details.setTran_Item_ID(rs.getString("TRAN_ITEM_ID"));
                        details.setTransaction_Item_key(rs.getString("TRANSACTION_ITEM_KEY"));
                        details.setTran_Code(rs.getString("TRAN_CODE"));
                        details.setAccount(rs.getString("ACCOUNT"));
                        details.setDr_Cr_Flag(rs.getString("DR_CR_FLAG"));
                        details.setTransaction_Amount(rs.getString("TRANSACTION_AMOUNT"));
                        details.setCurrency(rs.getString("CURRENCY"));
                        details.setNarrative(rs.getString("NARRATIVE"));
                        details.setBase_Equivalent(rs.getString("BASE_EQUIVALENT"));
                        details.setSource_Branch(rs.getString("SOURCE_BRANCH"));
                        details.setDate_Created(rs.getString("DATE_CREATED"));
                        details.setAccount_No_Req(rs.getString("ACCOUNT_NO_REQ"));
                        details.setTransactionID(rs.getString("TRANSACTIONID"));
                        details.setPosting_Date(rs.getString("POSTING_DATE"));
                        details.setAccount_Number_Cbs(rs.getString("ACCOUNT_NUMBER_CBS"));

                        return details;
                    }
                })
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                    new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                    new SqlParameter("I_TRANSACTION_REF", Types.VARCHAR),
                    new SqlOutParameter("O_TRAN_REQ_ID", Types.VARCHAR),
                    new SqlOutParameter("O_TRANSACTION_ID", Types.VARCHAR),
                    new SqlOutParameter("O_MESSAGE_ID", Types.VARCHAR),
                    new SqlOutParameter("O_MESSAGE_TYPE", Types.VARCHAR),
                    new SqlOutParameter("O_STATUS_FLAG", Types.VARCHAR),
                    new SqlOutParameter("O_MIRROR_STATUS", Types.VARCHAR),
                    new SqlOutParameter("O_CHANNEL_ID", Types.VARCHAR),
                    new SqlOutParameter("O_VALUE_DATE", Types.VARCHAR),
                    new SqlOutParameter("O_FROM_CURRENCY", Types.VARCHAR),
                    new SqlOutParameter("O_TO_CURRENCY", Types.VARCHAR),
                    new SqlOutParameter("O_EXCHANGE_RATE", Types.VARCHAR),
                    new SqlOutParameter("O_EXCHANGE_RATE_FLAG", Types.VARCHAR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR),
                    new SqlOutParameter("O_TRANSACTION_DETAILS", Types.REF_CURSOR));

            Map<String, Object> parameters = new HashMap<String, Object>();
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_ENTITY_ID", transactionQueryReq.getRequestBody().getEntityID());
            inParamMap.put("I_TRANSACTION_REF", transactionQueryReq.getRequestBody().getTransactionRefNo());

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            if (out == null) {
                //resStr = Collections.emptyList();
                header.setResponseCode(1);
                header.setResponseMessage("Error");
                transactionQueryResponse.setResponseCode(1);
                transactionQueryResponse.setResponseMessage("No Valid Response");
            } else {
                resMessageCodeStr = (int) out.get("O_MESSAGE_CODE");
                resMessageDescriptionStr = (String) out.get("O_MESSAGE_DESCRIPTION");

                //Check for Error Output
                if(resMessageCodeStr == 0) {

                    header.setResponseCode(0);
                    header.setResponseMessage("Success");

                    transactionQueryResponse.setResponseCode(0);
                    transactionQueryResponse.setResponseMessage("Success");

                    //Start with Transaction Items Header
                    tranQueryDbResponse.setTran_Req_ID((String) out.get("O_TRAN_REQ_ID"));
                    tranQueryDbResponse.setTransaction_ID((String) out.get("O_TRANSACTION_ID"));
                    tranQueryDbResponse.setMessage_ID((String) out.get("O_MESSAGE_ID"));
                    tranQueryDbResponse.setMessage_Type((String) out.get("O_MESSAGE_TYPE"));
                    tranQueryDbResponse.setStatus_Flag((String) out.get("O_STATUS_FLAG"));
                    tranQueryDbResponse.setMirror_Status((String) out.get("O_MIRROR_STATUS"));
                    tranQueryDbResponse.setChannel_ID((String) out.get("O_CHANNEL_ID"));
                    tranQueryDbResponse.setValue_Date((String) out.get("O_VALUE_DATE"));
                    tranQueryDbResponse.setEntity_ID((String) out.get("O_ENTITY_ID"));
                    tranQueryDbResponse.setFrom_Currency((String) out.get("O_FROM_CURRENCY"));
                    tranQueryDbResponse.setTo_Currency((String) out.get("O_TO_CURRENCY"));
                    tranQueryDbResponse.setExchange_Rate((String) out.get("O_EXCHANGE_RATE"));
                    tranQueryDbResponse.setExchange_Rate_Flag((String) out.get("O_EXCHANGE_RATE_FLAG"));

                    /*List<ExchangeRateDetails> exchangeRateDetails = new ArrayList<>();;
                        ExchangeRateDetails erate = new ExchangeRateDetails();
                        erate.setFromCurrency(out.get("O_FROM_CURRENCY").toString());
                        erate.setToCurrency(out.get("O_TO_CURRENCY").toString());
                        erate.setExchangeRate(out.get("O_EXCHANGE_RATE").toString());
                        erate.setExchangeRateFlag(out.get("O_EXCHANGE_RATE_FLAG").toString());
                    */
                    //Add the Exchange Rates
                    //exchangeRateDetails.add(erate);
                    //tranQueryDbResponse.setExchangeRateDetails(exchangeRateDetails);

                    //Add the Transaction Items
                    List<TransactionItems> transactionListing = new ArrayList<>();
                    transactionListing = (List) out.get("O_TRANSACTION_DETAILS");
                    tranQueryDbResponse.setTransactionDetails(transactionListing);

                    transactionQueryResponse.setResponseBody(tranQueryDbResponse);
                }
                else {
                    header.setResponseCode(-1);
                    header.setResponseMessage("Error");
                }
                transactionQueryResponse.setResponseCode(resMessageCodeStr);
                transactionQueryResponse.setResponseMessage(resMessageDescriptionStr);
            }
        } catch (Exception ex) {
            logger.error(QueryTransactionDb.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
            transactionQueryResponse.setResponseCode(1);
            transactionQueryResponse.setResponseMessage("Error Querying Transaction: See application logs");
        }

        return transactionQueryResponse;
    }
}
