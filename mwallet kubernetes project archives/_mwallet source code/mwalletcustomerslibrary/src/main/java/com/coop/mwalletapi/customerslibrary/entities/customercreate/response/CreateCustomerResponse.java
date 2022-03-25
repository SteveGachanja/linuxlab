package com.coop.mwalletapi.customerslibrary.entities.customercreate.response;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class CreateCustomerResponse extends ResponseHeader {
    CreateCustomerResponseData responseBody;
}
