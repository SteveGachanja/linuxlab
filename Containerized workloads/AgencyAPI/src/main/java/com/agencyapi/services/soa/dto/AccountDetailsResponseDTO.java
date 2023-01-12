package com.agencyapi.services.soa.dto;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class AccountDetailsResponseDTO extends ResponseHeader {
    private String accountNumber;
    private String accountName;
    private String branchName;
    private String phoneNumber;
    private String email;
    private String customerID;
    private String availableBalance;
    private String accStatus;
    private String currencyCode;
    private String branchCode;
}
