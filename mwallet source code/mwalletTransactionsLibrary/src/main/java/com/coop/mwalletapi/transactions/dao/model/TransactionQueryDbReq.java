package com.coop.mwalletapi.transactions.dao.model;

import lombok.Data;

/**
 * @author fosano
 */

@Data
public class TransactionQueryDbReq {
    String entityID;
    String makerID;
    String makerDateTime;
    String approverID;
    String approverDateTime;
    String transactionRefNo;
}
