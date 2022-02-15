package com.coop.mwalletapi.transactions.dao.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fosano
 */

@Data
public class TransactionQueryDbResp {
    String tran_Req_ID;
    String transaction_ID;
    String message_ID;
    String message_Type;
    String status_Flag;
    String mirror_Status;
    String channel_ID;
    String value_Date;
    String entity_ID;
    String from_Currency;
    String to_Currency;
    String exchange_Rate;
    String exchange_Rate_Flag;
    //List<ExchangeRateDetails> exchangeRateDetails = new ArrayList<>();;
    List<TransactionItems> TransactionDetails = new ArrayList<>();
}
