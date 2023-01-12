package com.agencyapi.services;

import com.agencyapi.dao.Terminals;
import com.agencyapi.entities.TerminalsReq;
import com.agencyapi.repositories.TerminalsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 *
 * @author okahia
 */

@Service
public class TerminalsService {
    @Autowired
    private TerminalsRepository tRepository;

    public Terminals getTerminalByTerminalId(TerminalsReq terminalReq) {
        Terminals tv = null;
        tv = tRepository.findByTerminalId(terminalReq.getRequestBody().getTerminalId());
        return tv;
    }
    
    
    
}
