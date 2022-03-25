package com.coop.mwalletapi.mwalletblockamountlibrary.entities;


import com.coop.mwalletapi.commons.ResponseHeader;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.BlockAmountRespData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fosano
 */

@Getter
@Setter
public class BlockAmountDbResponse extends ResponseHeader {
    //List<ResponseBody> responseDetails = new ArrayList<>(); //for arrays returned
    BlockAmountRespData responseBody;

}
