package com.coop.mwalletapi.customer.accountsummary.dao.model;

import com.coop.mwalletapi.commons.ResponseHeader;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author pkingongo
 */

@Getter
@Setter
public class AccountDataDbResp extends ResponseHeader {
    List<AccountDataDbDao> accountData;
}
