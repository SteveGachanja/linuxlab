
package com.agencyapi.repositories;

import com.agencyapi.dao.Terminals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author okahia
 */

@Repository
public interface TerminalsRepository extends JpaRepository<Terminals, Long> {
    Terminals findByTerminalId(String terminalId);
    //List<TerminalValidate> findByTerminalId(String terminalId);

}
