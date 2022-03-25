package com.coop.mwalletapi.customer.get.allaccounts.req;

import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class SuppliedCustomerGetAllAccountsData extends RequestHeader {
    RequestBody requestBody;
}
