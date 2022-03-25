package com.coop.mwalletapi.customer.get.allaccounts.resp;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class GetCustomerAllAccountsResponse extends ResponseHeader {
    GetCustomerAllAccountsResponseData responseBody;
}
