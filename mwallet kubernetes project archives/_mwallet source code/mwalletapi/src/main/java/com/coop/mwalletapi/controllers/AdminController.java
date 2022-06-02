/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author okahia
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/Admin", method = { RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminController {
    
    private static final Logger logger = LogManager.getLogger(CustomersController.class.getName());
    
    @Autowired
    private DataSource ds;
	
	@Autowired
    List<ApiUsers> au;

    @RequestMapping(value = "/CreateApiUser/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object CreateApiUser(HttpServletRequest request) throws IOException {
        Object retobj = null;
        try {
            
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            
            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj(jsonString);

            Class cls = Class.forName("com.coop.mwalletapi.adminlibrary.AdminLibrary");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("createApiUser", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

             retobj = meth.invoke(myObject, arglist);

        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(CustomersController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return retobj;
    }
    
    @RequestMapping(value = "/GetApiUsers/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object GetApiUsers(HttpServletRequest request) throws IOException {
        Object retobj = null;
        try {
            
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            
            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj(jsonString);

            Class cls = Class.forName("com.coop.mwalletapi.adminlibrary.AdminLibrary");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("getApiUsers", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

             retobj = meth.invoke(myObject, arglist);

        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(CustomersController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return retobj;
    }

    @RequestMapping(value = "/GetNationalities/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object GetNationalities(HttpServletRequest request) throws IOException {
        Object retobj = null;
        try {
            
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            
            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setInObj(jsonString);

            Class cls = Class.forName("com.coop.mwalletapi.adminlibrary.AdminLibrary");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("getNationalities", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

             retobj = meth.invoke(myObject, arglist);

        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(CustomersController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return retobj;
    }
	
	@RequestMapping(value = "/GetProducts/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object GetProducts(HttpServletRequest request) throws IOException {
        Object retobj = null;
        try {
            
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            
            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setApiUsers(au);
            mwalletCommonDataReq.setInObj(jsonString);

            Class cls = Class.forName("com.coop.mwalletapi.adminlibrary.AdminLibrary");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("getProducts", argTypes);
            Object myObject = cls.newInstance();

            Object arglist[] = new Object[1];
            arglist[0] = mwalletCommonDataReq;

             retobj = meth.invoke(myObject, arglist);

        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            logger.error(CustomersController.class.getName() + " MethodName: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " " + ex, ex);
        }

        return retobj;
    }
	
	@RequestMapping(value = "/GetSegments/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object GetSegments(HttpServletRequest request) throws IOException {
        Object retobj = null;
        try {
            
            String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            
            MwalletCommonDataReq mwalletCommonDataReq = new MwalletCommonDataReq();
            mwalletCommonDataReq.setMwalletDataSource(ds);
            mwalletCommonDataReq.setApiUsers(au);
            mwalletCommonDataReq.setInObj(jsonString);

            Class cls = Class.forName("com.coop.mwalletapi.adminlibrary.AdminLibrary");
            Class[] argTypes = new Class[] { MwalletCommonDataReq.class };

            Method meth = cls.getMethod("getSegments", argTypes);
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