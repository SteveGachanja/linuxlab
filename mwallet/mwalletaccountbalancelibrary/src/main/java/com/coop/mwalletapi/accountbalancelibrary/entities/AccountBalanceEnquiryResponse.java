package com.coop.mwalletapi.accountbalancelibrary.entities;


import com.coop.mwalletapi.accountbalancelibrary.dao.model.AccountBalanceEnquiryDataDbResp;
import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cndirangu
 */

@Getter
@Setter
public class AccountBalanceEnquiryResponse extends ResponseHeader {
  
    AccountBalanceEnquiryDataDbResp responseBody;
    
}
