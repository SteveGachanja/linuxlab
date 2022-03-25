package com.coop.mwalletapi.mwalletupdateaccountflaglibrary.dao.model;

import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author fosano
 */

@Data
public class UpdateAccountFlagReqData extends RequestHeader {
    //String requestID; //Auto-generated
    String entityID;
    String actionType;
    String accountNumber;
    String reason;
    String channelID;
    //String actionDate; //Default System DateTime
    String status;
    String makerID;
    String makerDate;
    String approverID;
    String approverDate;
    //Date recordDate; //Default System DateTime

}
