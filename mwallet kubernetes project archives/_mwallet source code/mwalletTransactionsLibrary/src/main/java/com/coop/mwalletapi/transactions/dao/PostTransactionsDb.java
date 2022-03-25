package com.coop.mwalletapi.transactions.dao;

import com.coop.mwalletapi.transactions.dao.model.TransactionsPostDbResp;
import com.coop.mwalletapi.transactions.entities.TransactionItem;
import com.coop.mwalletapi.transactions.entities.TransactionPostRequest;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

/**
 * @author okahia
 */
public class PostTransactionsDb {

    private static final Logger logger = LogManager.getLogger(PostTransactionsDb.class.getName());

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall simpleJdbcCallRefCursor;
    private DataSource ds;

    @Autowired
    public PostTransactionsDb(DataSource dsPassed) {
        this.jdbcTemplate = new JdbcTemplate(dsPassed);
        this.ds = dsPassed;
    }

    public TransactionsPostDbResp postTransactions(
            int apiUser, String transactionId, TransactionPostRequest postTransReq
    ) throws ClassNotFoundException, SQLException {
        TransactionsPostDbResp transactionsPostDbResp = new TransactionsPostDbResp();
        
        try {           
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            String approverDate = sdf.format(sdf.parse(postTransReq.getRequestBody().getOperationParameters().getApproverDateTime()));
            String makerDate = sdf.format(sdf.parse(postTransReq.getRequestBody().getOperationParameters().getMakerDateTime()));
            String valueDate = sdf.format(sdf.parse(postTransReq.getRequestBody().getOperationParameters().getValueDate()));
                        
            OracleConnection connection = null;
            try (Connection hikariCon = jdbcTemplate.getDataSource().getConnection()) {
                if (hikariCon.isWrapperFor(OracleConnection.class)) {
                   connection = hikariCon.unwrap(OracleConnection.class);
                }
            }
            
            //construct the Object for posting
            ArrayList < TransactionItem > transItemsIn = postTransReq.getRequestBody().getTransactionItems().getTransactionItem();
            
            Object[] tranItems = new Object[transItemsIn.size()];
            for (int i = 0; i < transItemsIn.size(); i++) {
                Object obj = new Object[]{
                    transItemsIn.get(i).getTransactionItemKey(), // + "-" + i,
                    transItemsIn.get(i).getAccountNumber(),
                    transItemsIn.get(i).getDebitCreditFlag(),
                    transItemsIn.get(i).getTransactionAmount(),
                    transItemsIn.get(i).getTransactionCurrency(),
                    transItemsIn.get(i).getTransactionItemKey(),
                    transItemsIn.get(i).getNarrative(),
                    transItemsIn.get(i).getTransactionAmount(),
                    transItemsIn.get(i).getSourceBranch(),
                    transItemsIn.get(i).getTransactionCode()
                };
                tranItems[i] = obj;
            }

            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName("M_WALLET_POSTING")
                    .withProcedureName("POST_TRANSACTIONS_BULK")
                    .declareParameters(
                            new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                            new SqlParameter("I_TRANSACTION_ID", Types.VARCHAR),
                            new SqlParameter("I_EXTERNAL_REFNO", Types.VARCHAR),
                            new SqlParameter("I_MESSAGE_ID", Types.VARCHAR),
                            new SqlParameter("I_MESSAGE_TYPE", Types.VARCHAR),
                            new SqlParameter("I_CHANNEL_ID", Types.VARCHAR),
                            new SqlParameter("I_VALUE_DATE", Types.DATE),
                            new SqlParameter("I_FROM_CURRENCY", Types.VARCHAR),
                            new SqlParameter("I_TO_CURRENCY", Types.VARCHAR),
                            new SqlParameter("I_EXCHANGE_RATE", Types.VARCHAR),
                            new SqlParameter("I_TYP_TBL_TRANSACTIONITEMS", OracleTypes.ARRAY), //OracleTypes.STRUCT),
                            new SqlParameter("I_MAKER_ID", Types.VARCHAR),
                            new SqlParameter("I_MAKER_DATE", Types.DATE),
                            new SqlParameter("I_APPROVER_ID", Types.VARCHAR),
                            new SqlParameter("I_APPROVER_DATE", Types.DATE),
                            new SqlOutParameter("O_TRAN_REQ_ID", Types.VARCHAR),
                            new SqlOutParameter("O_TRANSACTION_ID", Types.VARCHAR),
                            new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                            new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR));

                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("I_ENTITY_ID", apiUser);
                parameters.put("I_TRANSACTION_ID", transactionId);
                parameters.put("I_EXTERNAL_REFNO", postTransReq.getRequestBody().getOperationParameters().getTransactionID());
                parameters.put("I_MESSAGE_ID", postTransReq.getMessageId());
                parameters.put("I_MESSAGE_TYPE", postTransReq.getRequestBody().getOperationParameters().getMessageType());
                parameters.put("I_CHANNEL_ID", postTransReq.getChannelId());
                parameters.put("I_VALUE_DATE", valueDate);
                parameters.put("I_FROM_CURRENCY", postTransReq.getRequestBody().getOperationParameters().getExchangeRateDetails().getFromCurrency());
                parameters.put("I_TO_CURRENCY", postTransReq.getRequestBody().getOperationParameters().getExchangeRateDetails().getToCurrency());
                parameters.put("I_EXCHANGE_RATE", postTransReq.getRequestBody().getOperationParameters().getExchangeRateDetails().getExchangeRate());
                parameters.put("I_MAKER_ID", postTransReq.getRequestBody().getOperationParameters().getUserID());
                parameters.put("I_MAKER_DATE", makerDate);
                parameters.put("I_APPROVER_ID", postTransReq.getRequestBody().getOperationParameters().getApproverID());
                parameters.put("I_APPROVER_DATE", approverDate);

            Array arrayOfItems = ((OracleConnection) connection).createOracleArray("TYP_TBL_TRANSACTIONITEMS", tranItems);

            parameters.put("I_TYP_TBL_TRANSACTIONITEMS", arrayOfItems);
            parameters.put("O_TRAN_REQ_ID", "0");
            parameters.put("O_TRANSACTION_ID", "");
            parameters.put("O_MESSAGE_CODE", "");
            parameters.put("O_MESSAGE_DESCRIPTION", "");

            Map<String, Object> out = simpleJdbcCall.execute(parameters);
            String tranId = (String) out.get("O_TRAN_REQ_ID");
            String transactId = (String) out.get("O_TRANSACTION_ID");
            int messageCode = (int) out.get("O_MESSAGE_CODE");
            String messageDescription = (String) out.get("O_MESSAGE_DESCRIPTION");
            
            transactionsPostDbResp.setMessageId(postTransReq.getMessageId());
            transactionsPostDbResp.setResponseCode(messageCode);
            transactionsPostDbResp.setResponseMessage(messageDescription);
            transactionsPostDbResp.setTransactionID(transactId);
            
        } catch (Exception ex) {
            logger.error(PostTransactionsDb.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
            
            transactionsPostDbResp.setMessageId(postTransReq.getMessageId());
            transactionsPostDbResp.setResponseCode(1);
            transactionsPostDbResp.setResponseMessage("Error Posting: See application logs");
            transactionsPostDbResp.setTransactionID("");
        }
        
        return transactionsPostDbResp;
    }
    
    public TransactionsPostDbResp reverseTransactions(
            int apiUser, String transactionId, TransactionPostRequest ReverseTransReq
    ) throws ClassNotFoundException, SQLException {
        TransactionsPostDbResp transactionsReverseDbResp = new TransactionsPostDbResp();
        
        try {           
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            String approverDate = sdf.format(sdf.parse(ReverseTransReq.getRequestBody().getOperationParameters().getApproverDateTime()));
            String makerDate = sdf.format(sdf.parse(ReverseTransReq.getRequestBody().getOperationParameters().getMakerDateTime()));
            String valueDate = sdf.format(sdf.parse(ReverseTransReq.getRequestBody().getOperationParameters().getValueDate()));
                        
            OracleConnection connection = null;
            try (Connection hikariCon = jdbcTemplate.getDataSource().getConnection()) {
                if (hikariCon.isWrapperFor(OracleConnection.class)) {
                   connection = hikariCon.unwrap(OracleConnection.class);
                }
            }
            
            //construct the Object for Reverseing
            ArrayList < TransactionItem > transItemsIn = ReverseTransReq.getRequestBody().getTransactionItems().getTransactionItem();
            
            Object[] tranItems = new Object[transItemsIn.size()];
            for (int i = 0; i < transItemsIn.size(); i++) {
                Object obj = new Object[]{
                    transItemsIn.get(i).getTransactionItemKey(), // + "-" + i,
                    transItemsIn.get(i).getAccountNumber(),
                    transItemsIn.get(i).getDebitCreditFlag(),
                    transItemsIn.get(i).getTransactionAmount(),
                    transItemsIn.get(i).getTransactionCurrency(),
                    transItemsIn.get(i).getTransactionItemKey(),
                    transItemsIn.get(i).getNarrative(),
                    transItemsIn.get(i).getTransactionAmount(),
                    transItemsIn.get(i).getSourceBranch(),
                    transItemsIn.get(i).getTransactionCode()
                };
                tranItems[i] = obj;
            }

            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withCatalogName("M_WALLET_POSTING")
                    .withProcedureName("REVERSE_TRANSACTIONS")
                    .declareParameters(
                            new SqlParameter("I_ENTITY_ID", Types.NUMERIC),
                            new SqlParameter("I_TRANSACTION_ID", Types.VARCHAR),
                            new SqlParameter("I_EXTERNAL_REFNO", Types.VARCHAR),
                            new SqlParameter("I_MESSAGE_ID", Types.VARCHAR),
                            new SqlParameter("I_MESSAGE_TYPE", Types.VARCHAR),
                            new SqlParameter("I_CHANNEL_ID", Types.VARCHAR),
                            new SqlParameter("I_VALUE_DATE", Types.DATE),
                            new SqlParameter("I_FROM_CURRENCY", Types.VARCHAR),
                            new SqlParameter("I_TO_CURRENCY", Types.VARCHAR),
                            new SqlParameter("I_EXCHANGE_RATE", Types.VARCHAR),
                            new SqlParameter("I_TYP_TBL_TRANSACTIONITEMS", OracleTypes.ARRAY), //OracleTypes.STRUCT),
                            new SqlParameter("I_MAKER_ID", Types.VARCHAR),
                            new SqlParameter("I_MAKER_DATE", Types.DATE),
                            new SqlParameter("I_APPROVER_ID", Types.VARCHAR),
                            new SqlParameter("I_APPROVER_DATE", Types.DATE),
                            new SqlOutParameter("O_TRAN_REQ_ID", Types.VARCHAR),
                            new SqlOutParameter("O_TRANSACTION_ID", Types.VARCHAR),
                            new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                            new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR));

                Map<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("I_ENTITY_ID", apiUser);
                parameters.put("I_TRANSACTION_ID", transactionId);
                parameters.put("I_EXTERNAL_REFNO", ReverseTransReq.getRequestBody().getOperationParameters().getTransactionID());
                parameters.put("I_MESSAGE_ID", ReverseTransReq.getMessageId());
                parameters.put("I_MESSAGE_TYPE", ReverseTransReq.getRequestBody().getOperationParameters().getMessageType());
                parameters.put("I_CHANNEL_ID", ReverseTransReq.getChannelId());
                parameters.put("I_VALUE_DATE", valueDate);
                parameters.put("I_FROM_CURRENCY", ReverseTransReq.getRequestBody().getOperationParameters().getExchangeRateDetails().getFromCurrency());
                parameters.put("I_TO_CURRENCY", ReverseTransReq.getRequestBody().getOperationParameters().getExchangeRateDetails().getToCurrency());
                parameters.put("I_EXCHANGE_RATE", ReverseTransReq.getRequestBody().getOperationParameters().getExchangeRateDetails().getExchangeRate());
                parameters.put("I_MAKER_ID", ReverseTransReq.getRequestBody().getOperationParameters().getUserID());
                parameters.put("I_MAKER_DATE", makerDate);
                parameters.put("I_APPROVER_ID", ReverseTransReq.getRequestBody().getOperationParameters().getApproverID());
                parameters.put("I_APPROVER_DATE", approverDate);

            Array arrayOfItems = ((OracleConnection) connection).createOracleArray("TYP_TBL_TRANSACTIONITEMS", tranItems);

            parameters.put("I_TYP_TBL_TRANSACTIONITEMS", arrayOfItems);
            parameters.put("O_TRAN_REQ_ID", "0");
            parameters.put("O_TRANSACTION_ID", "");
            parameters.put("O_MESSAGE_CODE", "");
            parameters.put("O_MESSAGE_DESCRIPTION", "");

            Map<String, Object> out = simpleJdbcCall.execute(parameters);
            String tranId = (String) out.get("O_TRAN_REQ_ID");
            String transactId = (String) out.get("O_TRANSACTION_ID");
            int messageCode = (int) out.get("O_MESSAGE_CODE");
            String messageDescription = (String) out.get("O_MESSAGE_DESCRIPTION");
            
            transactionsReverseDbResp.setMessageId(ReverseTransReq.getMessageId());
            transactionsReverseDbResp.setResponseCode(messageCode);
            transactionsReverseDbResp.setResponseMessage(messageDescription);
            transactionsReverseDbResp.setTransactionID(transactId);
            
        } catch (Exception ex) {
            logger.error(PostTransactionsDb.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
            
            transactionsReverseDbResp.setMessageId(ReverseTransReq.getMessageId());
            transactionsReverseDbResp.setResponseCode(1);
            transactionsReverseDbResp.setResponseMessage("Error Reverseing: See application logs");
            transactionsReverseDbResp.setTransactionID("");
        }
        
        return transactionsReverseDbResp;
    }
}
