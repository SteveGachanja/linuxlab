package com.coop.mwalletapi.transactions.entities.resp;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class TransactionsPostResponse extends ResponseHeader {
    private String messageId;
    ResponseBody responseBody;
}
