package com.coop.mwalletapi.configs;

import com.coop.mwalletapi.commons.ApiUsers;
import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import java.lang.reflect.Method;
import java.util.List;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author okahia
 */

@Configuration
public class LoadApiUsers {
    private static final Logger logger = LogManager.getLogger(LoadApiUsers.class.getName());
    
    @Autowired
    private DataSource ds;
    
    @Bean
    public List<ApiUsers> loadActiveApiUsers(){
        
        List<ApiUsers> apiUsers = null;
        try
        {
            Object retobj = null;

            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj("");

            Class cls = Class.forName("com.coop.mwalletapi.adminlibrary.AdminLibrary");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("getApiUsersList", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

            retobj = meth.invoke(myObject, arglist);
            apiUsers = (List<ApiUsers>) retobj;
            
        } catch (Exception ex) {
            logger.error(LoadApiUsers.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        return apiUsers; 
    }
}
