
package com.agencyapi.repositories;

import com.agencyapi.dao.TerminalUsers;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author okahia
 */

@Repository
public interface TerminalUsersRepository extends JpaRepository<TerminalUsers, Long> {
    //TerminalUsers findByTerminalId(String terminalId);
    List<TerminalUsers> findByTerminalId(String terminalId);
    TerminalUsers findByTerminalIdAndUserName(String terminalId, String userName);
}
