package com.coop.mwalletapi.customerslibrary.entities.customeredit.response;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class EditCustomerResponse extends ResponseHeader {
    EditCustomerResponseData responseBody;
}
