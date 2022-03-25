package com.coop.mwalletapi.controllers;


import com.coop.mwalletapi.commons.ApiUsers;
import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/Accounts", method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerAccountSummaryController {
    
    private static final Logger logger = LogManager.getLogger(CustomerAccountSummaryController.class.getName());
    
    @Autowired
    private DataSource ds;

    @Autowired
    List<ApiUsers> au;

    @RequestMapping(value = "/GetCustomerAccountSummary/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object GetCustomerDetails(HttpServletRequest request) throws IOException{
        Object retobj = null;
        try
        {
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            
            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj(jsonString);
            mwalletCommonDataReq.setApiUsers(au);

            Class cls = Class.forName("com.coop.mwalletapi.customer.accountsummary.AccountSummaryLibrary");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("getCustomerAccounts", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

             retobj = meth.invoke(myObject, arglist);


        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(CustomerAccountSummaryController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        
        return retobj;
    }
	
	//added by Onesmus
	@RequestMapping(value = "/GetCustomerAllAccounts/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object GetCustomerAllAccounts(HttpServletRequest request) throws IOException{
        Object retobj = null;
        try
        {
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            
            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj(jsonString);
            mwalletCommonDataReq.setApiUsers(au);
            //mwalletCommonDataReq.setKafkaConfigs(kc);

            Class cls = Class.forName("com.coop.mwalletapi.customer.accountsummary.GetCustomerAllAccounts");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("getCustomerAllAccount", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

             retobj = meth.invoke(myObject, arglist);


        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(CustomersController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }
        
        return retobj;
    }
}


