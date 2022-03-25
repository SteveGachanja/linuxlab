package com.coop.mwalletapi.transactions.entities;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class TransactionItem {
    private String transactionItemKey;
    private String accountNumber;
    private String debitCreditFlag;
    private String transactionAmount;
    private String transactionCurrency;
    private String narrative;
    private String sourceBranch;
    private String transactionCode;
}
