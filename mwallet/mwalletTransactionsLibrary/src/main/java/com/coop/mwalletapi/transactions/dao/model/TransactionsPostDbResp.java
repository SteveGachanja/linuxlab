package com.coop.mwalletapi.transactions.dao.model;
import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;
/**
 * @author okahia
 */

@Data
public class TransactionsPostDbResp  extends ResponseHeader{
    String transactionID;
}
