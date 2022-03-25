package com.coop.mwalletapi.mwalletblockamountlibrary.entities;


import com.coop.mwalletapi.commons.RequestHeader;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.BlockAmountReqData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fosano
 */

@Getter
@Setter
public class BlockAmountDbRequest extends RequestHeader {
    //List<ResponseBody> responseDetails = new ArrayList<>(); //for arrays returned
    BlockAmountReqData requestBody;

}
