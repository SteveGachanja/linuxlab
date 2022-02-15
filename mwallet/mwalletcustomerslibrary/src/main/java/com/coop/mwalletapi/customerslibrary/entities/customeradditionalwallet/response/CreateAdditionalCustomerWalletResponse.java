package com.coop.mwalletapi.customerslibrary.entities.customeradditionalwallet.response;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class CreateAdditionalCustomerWalletResponse extends ResponseHeader {
    CreateAdditionalCustomerWalletResponseData responseBody;
}
