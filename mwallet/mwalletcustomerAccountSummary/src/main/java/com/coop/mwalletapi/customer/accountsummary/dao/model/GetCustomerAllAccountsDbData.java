package com.coop.mwalletapi.customer.accountsummary.dao.model;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetCustomerAllAccountsDbData {
    String documentNo;
    String mobileNo;
    String customerNumber;
    String accountName;
    String accountNumber;
    String accountAlias;
    String entityId;
    String entityName;
    String clearedBalance;
    String blockedBalance;
    String availableBalance;
    String currency;
    String productCode;
    String productName;
    String branchcode;
    String closed;
    String dormant;
    String debitLimit;
    String creditLimit;
    String debitAllowed;
    String creditAllowed;
    String maximumBalance;
    String createdOn;
    String createdBy;
    String authorisedOn;
    String authorisedBy;
    String status;
    String statusDesc;
}
