package com.agencyapi.repositories;

import com.agencyapi.dao.TerminalsTypes;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author okahia
 */

@Repository
public interface TerminalsTypesRepository extends JpaRepository<TerminalsTypes, Long> {
    List<TerminalsTypes> findAll();
    List<TerminalsTypes> findById(int id);
    List<TerminalsTypes> findByTerminalType(int id);


}
