package com.coop.mwalletapi.customerslibrary.dao.model;

import com.coop.mwalletapi.commons.ResponseHeader;
import java.util.List;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetCustomerDataDbResp extends ResponseHeader {
    List<GetCustomerDataDb> mCustData;
}
