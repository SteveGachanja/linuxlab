package com.agencyapi.controller;

import com.agencyapi.entities.admin.TerminalTypeAddReq;
import com.agencyapi.entities.admin.TerminalTypeByIdReq;
import com.agencyapi.entities.admin.TerminalTypeByTerminalIdReq;
import com.agencyapi.entities.admin.TerminalTypesResp;
import com.agencyapi.services.TerminalsTypeService;
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
@RequestMapping(value = "/Admin", method = { RequestMethod.POST, RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_VALUE)
public class ControllerAdmin {

    @Autowired
    TerminalsTypeService ttService;

    @RequestMapping(path = "/getAllTerminalTypes/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public TerminalTypesResp getTerminalTypes(@RequestBody TerminalTypeByIdReq terminalReq) {
        return ttService.getAllTerminals(terminalReq);
    }

    @RequestMapping(path = "/getTerminalTypesById/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public TerminalTypesResp getTerminalById(@RequestBody TerminalTypeByIdReq terminalReq) {
        //return ttService.getTerminalById(terminalReq);
        return ttService.getTerminalById(terminalReq);
    }

    @RequestMapping(path = "/getTerminalByTerminalType/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public TerminalTypesResp getTerminalByTerminalType(@RequestBody TerminalTypeByTerminalIdReq terminalReq) {
        return ttService.getTerminalByTerminalType(terminalReq);
    }

    @RequestMapping(path = "/AddTerminalTypes/v1.0", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public TerminalTypesResp AddTerminalTypes(@RequestBody TerminalTypeAddReq terminalReq) {
        return ttService.addTerminalType(terminalReq);
    }
}
