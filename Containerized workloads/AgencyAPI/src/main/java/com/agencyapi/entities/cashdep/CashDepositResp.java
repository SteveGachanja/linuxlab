package com.agencyapi.entities.cashdep;

import com.agencyapi.common.ResponseHeader;
import java.util.List;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class CashDepositResp  extends ResponseHeader{
    List<CashDepositDataResp> responseBody;
}
