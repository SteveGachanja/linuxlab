package com.agencyapi.services;

import com.agencyapi.dao.Agents;
import com.agencyapi.entities.agents.AgentsReq;
import com.agencyapi.repositories.AgentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 *
 * @author okahia
 */

@Service
public class AgentsService {
    @Autowired
    private AgentsRepository aRepository;

    public Agents getTerminalByTerminalId(AgentsReq agentsReq) {
        Agents a = null;
        a = aRepository.findByAgencyId(agentsReq.getRequestBody().getAgencyId());
        return a;
    }
    
    
    
}
