package com.coop.mwalletapi.mwalletupdateaccountflaglibrary.entities;


import com.coop.mwalletapi.commons.ResponseHeader;
import com.coop.mwalletapi.mwalletupdateaccountflaglibrary.dao.model.UpdateAccountFlagRespData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fosano
 */

@Getter
@Setter
public class UpdateAccountFlagDbResponse extends ResponseHeader {
    //List<ResponseBody> responseDetails = new ArrayList<>(); //for arrays returned
    UpdateAccountFlagRespData responseBody;

}
