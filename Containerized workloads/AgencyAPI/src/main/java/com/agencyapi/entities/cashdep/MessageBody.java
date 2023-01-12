package com.agencyapi.entities.cashdep;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class MessageBody {
    String acNo;
    String amount;
    String narration;
    String referenceNo;
}
