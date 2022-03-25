package com.coop.mwalletapi.customerslibrary.entities.customercreate;

import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class SuppliedCustomerCreateData extends RequestHeader {
    RequestBody requestBody;
}
