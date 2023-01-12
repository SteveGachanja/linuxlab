package com.agencyapi.services;

import com.agencyapi.dao.TerminalUsers;
import com.agencyapi.entities.TerminalUsersReq;
import com.agencyapi.repositories.TerminalUsersRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author okahia
 */

@Service
public class TerminalUsersService {
    @Autowired
    private TerminalUsersRepository tuRepository;

    public List<TerminalUsers> getTerminalUsersByTerminalId(TerminalUsersReq terminalReq) {
        List<TerminalUsers> tu = null;
        tu = tuRepository.findByTerminalId(terminalReq.getRequestBody().getTerminalId());
        return tu;
    }
    
    
    
}
