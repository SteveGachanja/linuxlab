package com.coop.mwalletapi.transactions.entities.resp;

import com.coop.mwalletapi.transactions.entities.*;
import lombok.Data;

/**
 * @author okahia
 */
@Data
public class ResponseBody {
    OperationParameters operationParameters;
    TransactionItems transactionItems;
}
