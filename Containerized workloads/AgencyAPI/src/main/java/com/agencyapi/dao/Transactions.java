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
@Table(name = "TRANSACTIONS")
public class Transactions implements Serializable {
    @Id @GeneratedValue
    @Column(name="TRAN_ID")
    private int tranId;

    @Column(name="AGENT_NO")
    private String agencyNo;

    @Column(name="TERMINAL_ID")
    private String terminalId;

    @Column(name="REFERENCE_NO")
    private String referenceNo;

    @Column(name="TRANSACTION_ID")
    private String transactionId;

    @Column(name="ACCOUNT_NUMBER")
    private String acNo;

    @Column(name="DR_CR_FLAG")
    private String drcrFlag;

    @Column(name="TRAN_AMOUNT")
    private double transAmount;

    @Column(name="CURRENCY")
    private String currencyCode;

    @Column(name="TRAN_TYPE")
    private String tranType;

    @Column(name="NARRATIVE")
    private String narrative;

    @Column(name = "SOURCE_BRANCH")
    private String branchCode;

    @Column(name = "POSTING_DATE")
    Date postingDate;
	
    @Column(name = "VALUE_DATE")
    Date valueDate;

    @Column(name="MESSAGE_ID")
    private String messageId;
}
