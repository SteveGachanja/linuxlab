package com.coop.mwalletapi.customer.get.allaccounts.resp;

import lombok.Data;

/**
 * @author okahia
 */
@Data
public class GetCustomerAllAccountsResp {
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
