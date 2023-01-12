
package com.agencyapi.repositories;

import com.agencyapi.dao.Agents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author okahia
 */

@Repository
public interface AgentsRepository extends JpaRepository<Agents, Long> {
    Agents findByAgencyId(String agencyId);
    //List<TerminalValidate> findByTerminalId(String terminalId);

}
