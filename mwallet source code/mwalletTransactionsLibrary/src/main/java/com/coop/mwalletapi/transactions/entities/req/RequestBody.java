package com.coop.mwalletapi.transactions.entities.req;

import com.coop.mwalletapi.transactions.entities.OperationParameters;
import com.coop.mwalletapi.transactions.entities.TransactionItems;
import lombok.Data;

/**
 * @author okahia
 */
@Data
public class RequestBody {
    OperationParameters operationParameters;
    TransactionItems transactionItems;
}
