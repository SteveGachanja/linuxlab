package com.coop.mwalletapi.chargeslibrary.dao;

import com.coop.mwalletapi.chargeslibrary.entities.ChargeDetails;
import com.coop.mwalletapi.chargeslibrary.dao.model.ChargesEnquiryDataDbResponse;
import com.coop.mwalletapi.commons.ResponseHeader;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
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

    public ChargesEnquiryDataDbResponse getMClientData(String transactionCode, String productCode, String branchCode, double amount, ResponseHeader header) {
        ChargesEnquiryDataDbResponse chargesDataDbResp = new ChargesEnquiryDataDbResponse();
        try {
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("GET_TRANSACTION_CHARGES")
                    .withCatalogName("M_WALLET_CHARGES")
                    .withoutProcedureColumnMetaDataAccess()
                    .declareParameters(
                            new SqlParameter("I_TRANSACTION_CODE", Types.VARCHAR),
                            new SqlParameter("I_PRODUCT_CODE", Types.VARCHAR),
                            new SqlParameter("I_BRANCH_CODE", Types.VARCHAR),
                            new SqlParameter("I_TRANSACTION_AMOUNT", Types.NUMERIC),
                            new SqlOutParameter("O_TRANSACTION_CODE", Types.VARCHAR),
                            new SqlOutParameter("O_CBS_GL_ACCOUNT", Types.VARCHAR),
                            new SqlOutParameter("O_WALLETGL", Types.VARCHAR),
                            new SqlOutParameter("O_CREDITACCREQUIRED", Types.NUMERIC),
                            new SqlOutParameter("O_TRANSACTION_AMOUNT", Types.NUMERIC),
                            new SqlOutParameter("O_CHARGE_DETAILS", Types.REF_CURSOR),
                            new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                            new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
                    );

            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_TRANSACTION_CODE", transactionCode);
            inParamMap.put("I_PRODUCT_CODE", productCode);
            inParamMap.put("I_BRANCH_CODE", branchCode);
            inParamMap.put("I_TRANSACTION_AMOUNT", amount);

            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            Map<String, Object> out = simpleJdbcCallRefCursor.execute(paramaters);

            List<ChargeDetails> chargeDetails = new ArrayList<>();

            if (out == null) {
                header.setResponseCode(1);
                header.setResponseMessage("No Data Found");

            } else {
                header.setResponseCode((int) out.get("O_MESSAGE_CODE"));
                header.setResponseMessage((String) out.get("O_MESSAGE_DESCRIPTION"));
                chargesDataDbResp.setCbsGLAccount((String) out.get("O_CBS_GL_ACCOUNT"));
                chargesDataDbResp.setCreditAccountRequired(((BigDecimal) out.getOrDefault("O_CREDITACCREQUIRED", "0")).intValue());
                chargesDataDbResp.setTransactionAmount(((BigDecimal) out.getOrDefault("O_TRANSACTION_AMOUNT", "0")).doubleValue());
                chargesDataDbResp.setTransactionCode((String) out.get("O_TRANSACTION_CODE"));
                chargesDataDbResp.setWalletGl((String) out.get("O_WALLETGL"));
                if (header.getResponseCode() == 0) {
                    chargeDetails = (List) out.get("O_CHARGE_DETAILS");
                    chargesDataDbResp.setChargeDetails(chargeDetails);
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
