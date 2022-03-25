package com.coop.mwalletapi.customer.accountsummary.dao.model;

import com.coop.mwalletapi.commons.ResponseHeader;
import java.util.List;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetCustomerAllAccountsDbResp extends ResponseHeader {
    List<GetCustomerAllAccountsDbData> accountsData;
}
