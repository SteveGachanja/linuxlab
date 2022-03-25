package com.coop.mwalletapi.mwalletblockamountlibrary.dao.model;

import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author fosano
 */

@Data
public class UnblockAmountReqData extends RequestHeader {
    Long requestID; //Auto-generated
    String entityID;
    String actionType;
    String accountNumber;
    Double blockAmount;
    String reason;
    //String channelID;
    //Date blockDate;
    String externalReference;
    //String active;
    String status;
    String makerID;
    String makerDate;
    String approverID;
    String approverDate;

}
