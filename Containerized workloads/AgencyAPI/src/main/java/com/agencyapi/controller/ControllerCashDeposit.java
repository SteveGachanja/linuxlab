package com.agencyapi.controller;

import com.agencyapi.entities.cashdep.CashDepositReq;
import com.agencyapi.entities.cashdep.CashDepositResp;
import com.agencyapi.entities.cashdep.CashDepositValidateResp;
import com.agencyapi.services.CashDepositService;
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
public class ControllerCashDeposit {
    @Autowired
    CashDepositService cd;

    @RequestMapping(value = "/CashDepositValidate/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CashDepositValidateResp CashDeposit(@RequestBody CashDepositReq terminalReq) throws IOException, Exception {
        CashDepositValidateResp terminalResp = new CashDepositValidateResp();
        terminalResp =  cd.cashDepositValidate(terminalReq);
        return terminalResp;
    }

    @RequestMapping(value = "/CashDepositPost/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CashDepositResp CashDepositPost(@RequestBody CashDepositReq terminalReq) throws IOException, Exception {
        CashDepositResp terminalResp = new CashDepositResp();
        terminalResp =  cd.cashDepositPost(terminalReq);
        return terminalResp;
    }
}
