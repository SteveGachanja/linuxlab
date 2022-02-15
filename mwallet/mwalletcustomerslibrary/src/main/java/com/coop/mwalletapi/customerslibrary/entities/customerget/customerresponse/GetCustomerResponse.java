package com.coop.mwalletapi.customerslibrary.entities.customerget.customerresponse;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class GetCustomerResponse extends ResponseHeader {
    GetCustomerResponseData responseBody;
}
