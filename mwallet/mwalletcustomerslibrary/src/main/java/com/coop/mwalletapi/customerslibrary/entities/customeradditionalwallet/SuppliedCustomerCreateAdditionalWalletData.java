package com.coop.mwalletapi.customerslibrary.entities.customeradditionalwallet;

import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class SuppliedCustomerCreateAdditionalWalletData extends RequestHeader {
    RequestBody requestBody;
}
