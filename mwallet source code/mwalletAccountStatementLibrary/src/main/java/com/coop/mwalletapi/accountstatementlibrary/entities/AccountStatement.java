/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coop.mwalletapi.accountstatementlibrary.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author cndirangu
 */
@Getter
@Setter
public class AccountStatement implements Serializable {
    String  transactionId;
    String transactionItemId;
    String transactionCode;
    String transactionReference;
    String accountNumber;
    String accountName;
    String accountNumberIntl;
    String entityId;
    String transactionType; 
    String channelId;
    String debitAmount;
    String creditAmount;
    String transactionAmount;
    String currency;
    String messageId;
    String narration;
    String baseEquivalent;
    String runningClearedBalance;
    String runningBookBalance;
    String debitLimit;
    String sourceBranch;
    String transactionDate;
    String valueDate;
    String accountNumberCBS;
//
//    String TRANSACTIONID;
//    String TRAN_ITEM_ID;
//    String CODE;
//    String TRANSACTIONREFERENCE;
//    String ACCOUNTNUMBER;
//    String ACCOUNTNAME;
//    String ACCOUNT_NUMBER_INTL;
//    String ENTITY_ID;
//    String TRANSACTIONTYPE;
//    String DEBITAMOUNT;
//    String CREDITAMOUNT;
//    String TRAN_AMOUNT;
//    String CURRENCY;
//    String MESSAGE_ID;
//    String NARRATION;
//    String BASE_EQUIVALENT;
//    String RUNNINGCLEAREDBALANCE;
//    String RUNNINGBOOKBALANCE;
//    String DEBITLIMIT;
//    String SOURCE_BRANCH;
//    String TRANSACTIONDATE;
//    String VALUEDATE;
//    String ACCOUNT_NUMBER_CBS;
//
//    public String getVALUEDATE(String VALUEDATE) {
//        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(VALUEDATE)), ZoneId.systemDefault());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
//        return sdf.format(dateTime);
//    }
}
