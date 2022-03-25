/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coop.mwalletapi.accountstatementlibrary.dao;

import com.coop.mwalletapi.accountstatementlibrary.entities.AccountStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author cndirangu
 */
public class TransactionsListRowMapper implements RowMapper<AccountStatement> {
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
//    Functions Func = new Functions();
    @Override
    public AccountStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountStatement ussdAccountsList = new AccountStatement();
 
        ussdAccountsList.setTransactionId(rs.getString("TRANSACTIONID"));
        ussdAccountsList.setTransactionItemId(rs.getString("TRAN_ITEM_ID"));
        ussdAccountsList.setTransactionCode(rs.getString("CODE"));
        ussdAccountsList.setTransactionReference(rs.getString("TRANSACTIONREFERENCE"));
        ussdAccountsList.setAccountNumber(rs.getString("ACCOUNTNUMBER"));
        ussdAccountsList.setAccountName(rs.getString("ACCOUNTNAME"));
        ussdAccountsList.setAccountNumberIntl(rs.getString("ACCOUNT_NUMBER_INTL"));
        ussdAccountsList.setEntityId(rs.getString("ENTITY_ID"));
        ussdAccountsList.setTransactionType(rs.getString("TRANSACTIONTYPE"));
        ussdAccountsList.setDebitAmount(rs.getString("DEBITAMOUNT"));
        ussdAccountsList.setCreditAmount(rs.getString("CREDITAMOUNT"));
        ussdAccountsList.setTransactionAmount(rs.getString("TRAN_AMOUNT"));
        ussdAccountsList.setCurrency(rs.getString("CURRENCY"));
        ussdAccountsList.setMessageId(rs.getString("MESSAGE_ID"));
        ussdAccountsList.setNarration(rs.getString("NARRATION"));
        ussdAccountsList.setBaseEquivalent(rs.getString("BASE_EQUIVALENT"));        
        ussdAccountsList.setRunningClearedBalance(rs.getString("RUNNINGCLEAREDBALANCE"));
        ussdAccountsList.setRunningBookBalance(rs.getString("RUNNINGBOOKBALANCE"));
        ussdAccountsList.setDebitLimit(rs.getString("DEBITLIMIT"));
        ussdAccountsList.setSourceBranch(rs.getString("SOURCE_BRANCH"));
        ussdAccountsList.setTransactionDate(sdf.format(rs.getTimestamp("TRANSACTIONDATE")));
        ussdAccountsList.setValueDate(sdf.format(rs.getTimestamp("VALUEDATE")));
        ussdAccountsList.setAccountNumberCBS(rs.getString("ACCOUNT_NUMBER_CBS"));
        ussdAccountsList.setChannelId(rs.getString("CHANNEL_ID"));
        
        return ussdAccountsList;
    }
    
}

