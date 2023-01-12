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
@Table(name = "TERMINAL_USERS")
public class TerminalUsers implements Serializable {
    @Id @GeneratedValue
    @Column(name="ID")
    private int id;

    @Column(name="TERMINAL_ID")
    private String terminalId;

    @Column(name="USER_NAME")
    private String userName;

    @Column(name="PASSWORD")
    private String password;

    @Column(name="PIN_NO")
    private String pinNo;

    @Column(name="SESSION_TOKEN")
    private String sessionToken;

    @Column(name = "LAST_LOGIN_DATE")
    Date lastLoginDate;
    
    @Column(name="STATUS")
    private int status;

    @Column(name="USER_TYPE")
    private int userType;

    @Column(name="MAKER")
    private String maker;

    @Column(name = "MAKER_DATE")
    Date makerDate;

    @Column(name="MODIFIED_BY")
    private String modifiedBy;
	
    @Column(name = "MODIFIED_DATE")
    Date modifiedDate;

}
