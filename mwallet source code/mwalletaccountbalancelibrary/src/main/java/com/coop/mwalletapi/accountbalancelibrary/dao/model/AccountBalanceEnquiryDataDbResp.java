package com.coop.mwalletapi.accountbalancelibrary.dao.model;

import com.coop.mwalletapi.commons.ResponseHeader;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cndirangu
 */
@Getter
@Setter
public class AccountBalanceEnquiryDataDbResp extends ResponseHeader {

    String accountAlias;
    String entityId;
    String accountNumber;
    String accountName;
    double bookedBalance;
    double clearedBalance;
    double blockedBalance;
    double availableBalance;
    double maximumBalance;
    double arrearsAmount = 0;
    String branchSortCode;
    String branchName;
    String productCode;
    String productId;
    String productName;
    double debitLimit;
    double creditLimit;
    String currency;
}
