package com.coop.mwalletapi.mwalletblockamountlibrary.dao.model;

import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author fosano
 */

@Data
public class BlockedAmountQueryReqData extends RequestHeader {
    String actionType;
    String active;
    String accountNumber;

}
