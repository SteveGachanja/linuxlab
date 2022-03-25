package com.coop.mwalletapi.transactions.entities;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class OperationParameters {
    private String transactionID;
    private String messageType;
    private String userID;
    private String makerDateTime;
    private String approverID;
    private String approverDateTime;
    private String valueDate;
    ExchangeRateDetails exchangeRateDetails;
}
