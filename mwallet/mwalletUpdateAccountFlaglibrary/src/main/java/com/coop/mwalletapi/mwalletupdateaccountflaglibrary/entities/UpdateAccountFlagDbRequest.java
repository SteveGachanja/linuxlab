package com.coop.mwalletapi.mwalletupdateaccountflaglibrary.entities;


import com.coop.mwalletapi.commons.RequestHeader;
import com.coop.mwalletapi.mwalletupdateaccountflaglibrary.dao.model.UpdateAccountFlagReqData;
import lombok.Getter;
import lombok.Setter;

/**
 * @author fosano
 */

@Getter
@Setter
public class UpdateAccountFlagDbRequest extends RequestHeader {
    //List<ResponseBody> responseDetails = new ArrayList<>(); //for arrays returned
    UpdateAccountFlagReqData requestBody;

}
