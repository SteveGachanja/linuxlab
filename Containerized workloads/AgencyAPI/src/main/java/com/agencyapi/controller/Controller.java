package com.agencyapi.controller;

import com.agencyapi.entities.acc.AccountBalanceReq;
import com.agencyapi.entities.acc.AccountBalanceResp;
import com.agencyapi.entities.validate.TerminalValidateReq;
import com.agencyapi.entities.validate.TerminalValidateResp;
import com.agencyapi.services.AccountBalanceService;
import com.agencyapi.services.TerminalsValidateService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author okahia
 */

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(value = "/Api", method = { RequestMethod.POST, RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
public class Controller {

    @Autowired
    TerminalsValidateService tv;

    @Autowired
    AccountBalanceService acbal;

    @RequestMapping(value = "/TerminalValidate/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public TerminalValidateResp TerminalValidate(@RequestBody TerminalValidateReq terminalReq) throws IOException, Exception {
        TerminalValidateResp terminalResp = new TerminalValidateResp();
        terminalResp =  tv.validateTerminal(terminalReq);
        return terminalResp;
    }


    @RequestMapping(value = "/AccountBalanceGet/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public AccountBalanceResp TerminalValidate(@RequestBody AccountBalanceReq Req) throws IOException, Exception {
        AccountBalanceResp acbalResp = new AccountBalanceResp();
        acbalResp =  acbal.getAccountBalance(Req);
        return acbalResp;
    }
    
    

}
