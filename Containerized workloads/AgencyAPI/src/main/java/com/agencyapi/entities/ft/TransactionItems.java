package com.agencyapi.entities.ft;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class TransactionItems {
    String transactionItemKey;
    String accountNumber;
    String debitCreditFlag;
    String transactionAmount;
    String transactionCurrency;
    String transactionReference;
    String narrative;
    String baseEquivalent;
    String sourceBranch;
    String transactionCode;
}
