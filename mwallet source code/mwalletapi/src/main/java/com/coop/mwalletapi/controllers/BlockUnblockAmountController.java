package com.coop.mwalletapi.controllers;

import com.coop.mwalletapi.commons.ApiUsers;
import com.coop.mwalletapi.commons.MwalletCommonDataReq;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/Account", method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
public class BlockUnblockAmountController {

    private static final Logger logger = LogManager.getLogger(BlockUnblockAmountController.class.getName());

    @Autowired
    private DataSource ds;

    @Autowired
    List<ApiUsers> au;

    @RequestMapping(value = "/BlockAmount/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object BlockAmount(HttpServletRequest request) throws IOException{
        Object retobj = null;
        try
        {
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj(jsonString);
            mwalletCommonDataReq.setApiUsers(au);

            Class cls = Class.forName("com.coop.mwalletapi.mwalletblockamountlibrary.services.BlockUnblockAmountService");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("blockAmount", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

            retobj = meth.invoke(myObject, arglist);


        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(BlockUnblockAmountController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return retobj;
    }

    @RequestMapping(value = "/UnblockAmount/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object UnblockAmount(HttpServletRequest request) throws IOException{
        Object retobj = null;
        try
        {
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj(jsonString);
            mwalletCommonDataReq.setApiUsers(au);

            Class cls = Class.forName("com.coop.mwalletapi.mwalletblockamountlibrary.services.BlockUnblockAmountService");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("unblockAmount", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

            retobj = meth.invoke(myObject, arglist);


        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(BlockUnblockAmountController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return retobj;
    }

    @RequestMapping(value = "/BlockedAmountQuery/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object BlockedAmountQuery(HttpServletRequest request) throws IOException{
        Object retobj = null;
        try
        {
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj(jsonString);
            mwalletCommonDataReq.setApiUsers(au);

            Class cls = Class.forName("com.coop.mwalletapi.mwalletblockamountlibrary.services.BlockUnblockAmountService");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("blockedAmountQuery", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

            retobj = meth.invoke(myObject, arglist);


        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(BlockUnblockAmountController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return retobj;
    }

}


