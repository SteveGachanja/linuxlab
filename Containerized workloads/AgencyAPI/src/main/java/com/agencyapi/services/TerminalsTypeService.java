package com.agencyapi.services;

import com.agencyapi.dao.TerminalsTypes;
import com.agencyapi.entities.admin.TerminalTypeAddReq;
import com.agencyapi.entities.admin.TerminalTypeByIdReq;
import com.agencyapi.entities.admin.TerminalTypeByTerminalIdReq;
import com.agencyapi.entities.admin.TerminalTypesResp;
import com.agencyapi.repositories.TerminalsTypesRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 *
 * @author okahia
 */

@Service
public class TerminalsTypeService {
    @Autowired
    private TerminalsTypesRepository ttypeRepository;

    public TerminalTypesResp getAllTerminals(TerminalTypeByIdReq terminalReq) {
        TerminalTypesResp terminalResp = new TerminalTypesResp();
        List<TerminalsTypes> tt = null;
        tt = ttypeRepository.findAll();

        if (tt == null) {
            terminalResp.setResponseCode(1);
            terminalResp.setResponseMessage("No Data Found");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(Collections.emptyList());
        } else {
            terminalResp.setResponseCode(0);
            terminalResp.setResponseMessage("Success");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(tt);
        }

        return terminalResp;
    }

    public TerminalTypesResp getTerminalById(TerminalTypeByIdReq terminalReq) {
        TerminalTypesResp terminalResp = new TerminalTypesResp();
        List<TerminalsTypes> tt = null;
        tt = ttypeRepository.findById(terminalReq.getRequestBody().getId());

        if (tt == null) {
            terminalResp.setResponseCode(1);
            terminalResp.setResponseMessage("No Data Found");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(Collections.emptyList());
        } else {
            terminalResp.setResponseCode(0);
            terminalResp.setResponseMessage("Success");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(tt);
        }

        return terminalResp;
    }

    public TerminalTypesResp getTerminalByTerminalType(TerminalTypeByTerminalIdReq terminalReq) {
        TerminalTypesResp terminalResp = new TerminalTypesResp();
        List<TerminalsTypes> tt = null;
        tt = ttypeRepository.findByTerminalType(terminalReq.getRequestBody().getTerminalId());

        if (tt == null) {
            terminalResp.setResponseCode(1);
            terminalResp.setResponseMessage("No Data Found");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(Collections.emptyList());
        } else {
            terminalResp.setResponseCode(0);
            terminalResp.setResponseMessage("Success");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(tt);
        }

        return terminalResp;
    }
    
    public TerminalTypesResp addTerminalType(TerminalTypeAddReq terminalReq) {
        TerminalTypesResp terminalResp = new TerminalTypesResp();
        List<TerminalsTypes> ts = new ArrayList<>();
        List<TerminalsTypes> tt = null;

        terminalReq.getRequestBody().forEach(items -> {
            TerminalsTypes t = new TerminalsTypes();
            t.setId(items.getTerminalType());
            t.setTerminalType(items.getTerminalType());
            t.setTerminalTypeDesc(items.getTerminalTypeDesc());
            ts.add(t);
            
        });

        if (!ts.isEmpty()){
            ttypeRepository.saveAll(ts);
        }

        tt = ttypeRepository.findAll();

        if (tt == null) {
            terminalResp.setResponseCode(1);
            terminalResp.setResponseMessage("No Data Found");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(Collections.emptyList());
        } else {
            terminalResp.setResponseCode(0);
            terminalResp.setResponseMessage("Success");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(tt);
        }

        return terminalResp;
    }
    
}
