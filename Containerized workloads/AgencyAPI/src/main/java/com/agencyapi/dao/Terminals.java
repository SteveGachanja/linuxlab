package com.agencyapi.dao;

import java.io.Serializable;
import java.util.Date;
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
@Table(name = "TERMINALS")
public class Terminals implements Serializable {
    @Id @GeneratedValue
    @Column(name="ID")
    private int id;

    @Column(name="AGENCY_ID")
    private String agencyId;

    @Column(name="TERMINAL_ID")
    private String terminalId;

    @Column(name="MOBILE_NO")
    private String mobileNo;

    @Column(name="AC_NO_FLOAT")
    private String acNoFloat;

    @Column(name="AC_NO_COMM")
    private String acNoComm;

    @Column(name="AGENT_NO")
    private String agentNo;

    @Column(name="AGENT_TYPE")
    private int userType;

    @Column(name="TERMINAL_TYPE")
    private int terminalType;

    @Column(name="STATUS")
    private int status;

    @Column(name="MAKER")
    private String maker;

    @Column(name = "MAKER_DATE")
    Date makerDate;

    @Column(name="MODIFIED_BY")
    private String modifiedBy;
	
    @Column(name = "MODIFIED_DATE")
    Date modifiedDate;

}
