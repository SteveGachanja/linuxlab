package com.coop.mwalletapi.transactions.dao.model;


import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fosano
 */

@Getter
@Setter
public class TransactionQueryRequest extends RequestHeader {
    //List<ResponseBody> responseDetails = new ArrayList<>(); //for arrays returned
    TransactionQueryDbReq requestBody;
}
