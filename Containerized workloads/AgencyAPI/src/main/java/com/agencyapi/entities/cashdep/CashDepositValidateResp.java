package com.agencyapi.entities.cashdep;

import com.agencyapi.common.ResponseHeader;
import java.util.List;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class CashDepositValidateResp  extends ResponseHeader{
    List<CashDepositData> responseBody;
}
