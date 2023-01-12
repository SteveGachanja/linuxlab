package com.agencyapi.dao;

import com.agencyapi.entities.validate.TerminalValidateData;
import com.agencyapi.entities.validate.TerminalValidateReq;
import java.sql.Types;
import java.util.HashMap;
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
import org.springframework.stereotype.Controller;

/**
 * @author okahia
 */

@Controller
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
    
    public Map<String, Object> TerminalValidate(TerminalValidateReq terminalReq, String sessionToken) {
        Map<String, Object> out = null;
        try{
            simpleJdbcCallRefCursor = new SimpleJdbcCall(jdbcTemplate)
            .withProcedureName("TERMINALS_VALIDATE")
            .withCatalogName("TERMINALS_LOGIN_PACKAGE")
            .returningResultSet("O_TERMINALS", BeanPropertyRowMapper.newInstance(TerminalValidateData.class))
            .withoutProcedureColumnMetaDataAccess()
                    
            .declareParameters(
                    new SqlParameter("I_TERMINAL_ID", Types.VARCHAR),
                    new SqlParameter("I_SESSION_TOKEN", Types.VARCHAR),
                    new SqlOutParameter("O_TERMINALS", Types.REF_CURSOR),
                    new SqlOutParameter("O_MESSAGE_CODE", Types.INTEGER),
                    new SqlOutParameter("O_MESSAGE_DESCRIPTION", Types.VARCHAR)
            );
            
            Map<String, Object> inParamMap = new HashMap<String, Object>();
            inParamMap.put("I_TERMINAL_ID", terminalReq.getRequestBody().getTerminalId());
            inParamMap.put("I_SESSION_TOKEN", sessionToken);

            
            SqlParameterSource paramaters = new MapSqlParameterSource(inParamMap);
            out = simpleJdbcCallRefCursor.execute(paramaters);

        } catch (Exception ex) {
            logger.error(Db.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return out;
    }
    
    
            
}
