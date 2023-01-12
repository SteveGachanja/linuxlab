package com.agencyapi.entities.acc;

import com.agencyapi.common.ResponseHeader;
import java.util.List;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class AccountBalanceResp extends ResponseHeader{
    List<AccountBalanceData> responseBody;
}
