package com.coop.mwalletapi.customer.accountsummary.entities;


import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author pkingongo
 */

@Getter
@Setter
public class AccountResponse extends ResponseHeader {
  
    AccountResponseData responseBody;
    
}
