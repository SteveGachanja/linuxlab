package com.coop.mwalletapi.mwalletblockamountlibrary.entities;


import com.coop.mwalletapi.commons.RequestHeader;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.BlockedAmountQueryReqData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fosano
 */

@Getter
@Setter
public class BlockedAmountQueryDbRequest extends RequestHeader {
    //List<ResponseBody> responseDetails = new ArrayList<>(); //for arrays returned
    BlockedAmountQueryReqData requestBody;

}
