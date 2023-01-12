package com.agencyapi.services.soa.dto;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class AccountBalanceResponseDTO extends ResponseHeader {
    private String accountNumber;
    private String accountName;
    private String branchName;
    private String availableBalance;
    private String currencyCode;
    private String branchCode;
}
