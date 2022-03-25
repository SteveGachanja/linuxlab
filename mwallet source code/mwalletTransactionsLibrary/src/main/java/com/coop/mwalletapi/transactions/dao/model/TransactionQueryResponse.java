package com.coop.mwalletapi.transactions.dao.model;


import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fosano
 */

@Getter
@Setter
public class TransactionQueryResponse extends ResponseHeader {
    int responseCode;
    String responseMessage;
    String messageId;
    TransactionQueryDbResp responseBody;

}
