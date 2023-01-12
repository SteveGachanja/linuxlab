package com.agencyapi.entities.acc;

import com.agencyapi.common.RequestHeader;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class AccountBalanceReq extends RequestHeader{
    RequestBody requestBody;
}

