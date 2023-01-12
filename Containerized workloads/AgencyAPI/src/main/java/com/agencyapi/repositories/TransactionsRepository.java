
package com.agencyapi.repositories;

import com.agencyapi.dao.Transactions;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author okahia
 */

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {
    List<Transactions> findByTerminalId(String terminalId);
    List<Transactions> findByTerminalIdAndReferenceNo(String terminalId, String referenceNo);
}
