package com.agencyapi.entities.cashdep;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class CashDepositDataResp {
    String acNo;
    String amount;
    String narration;
    String referenceNo;
    String transactionID;
}
