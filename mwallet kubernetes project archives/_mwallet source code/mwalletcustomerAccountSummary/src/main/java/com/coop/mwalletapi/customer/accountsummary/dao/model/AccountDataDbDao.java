package com.coop.mwalletapi.customer.accountsummary.dao.model;

import lombok.Data;

/**
 * @author pkingongo
 */

@Data
public class AccountDataDbDao {   
   
    String customerNumber;
    String accountNumber;
    String documentNo;
    String clearedBalance;
    String blockedBalance;
    String availableBalance;
    String currency;
    String accountName;
    String productName;
    String branchcode;
    String productCode;
    String closed;
    String dormant;
    String debitLimit;
    String creditLimit;
    String debitAllowed;
    String creditAllowed;
    String maximumBalance;
    
    
}
