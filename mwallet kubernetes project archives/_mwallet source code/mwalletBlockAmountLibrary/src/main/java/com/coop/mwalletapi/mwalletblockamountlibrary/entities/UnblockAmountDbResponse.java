package com.coop.mwalletapi.mwalletblockamountlibrary.entities;


import com.coop.mwalletapi.commons.ResponseHeader;
import com.coop.mwalletapi.mwalletblockamountlibrary.dao.model.UnblockAmountRespData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fosano
 */

@Getter
@Setter
public class UnblockAmountDbResponse extends ResponseHeader {
    //List<ResponseBody> responseDetails = new ArrayList<>(); //for arrays returned
    UnblockAmountRespData responseBody;

}
