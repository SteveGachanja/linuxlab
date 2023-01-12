package com.agencyapi.services.soa.dto;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class FundsTransferResponseDTO extends ResponseHeader {
    private String messageReference;
    private String transactionID;
}
