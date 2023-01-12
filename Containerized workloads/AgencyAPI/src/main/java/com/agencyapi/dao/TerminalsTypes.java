
package com.agencyapi.dao;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Entity
@Data
@Table(name = "TR_TERMINALS_TYPES")
public class TerminalsTypes implements Serializable {
    @Id @GeneratedValue
    @Column(name="ID")
    private int id;

    @Column(name="TERMINAL_TYPE")
    private int terminalType;

    @Column(name="TERMINAL_TYPE_DESC")
    private String terminalTypeDesc;

}
