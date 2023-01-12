package com.agencyapi.entities.cashdep;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class CashDepositData {
    String acNo;
    String amount;
    String narration;
    String accName;
    String currencyCode;
    String accStatus;
    String referenceNo;
}
