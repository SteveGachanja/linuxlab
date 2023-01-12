package com.agencyapi.entities.cashdep;

import com.agencyapi.common.RequestHeader;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class CashDepositReq  extends RequestHeader{
    RequestBody requestBody;
}
