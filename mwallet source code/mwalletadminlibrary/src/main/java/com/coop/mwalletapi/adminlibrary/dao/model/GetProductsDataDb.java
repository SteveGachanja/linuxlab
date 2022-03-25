package com.coop.mwalletapi.adminlibrary.dao.model;

import lombok.Data;

/**
 * @author okahia
 */
@Data
public class GetProductsDataDb {
    String productId;
    String productCode;
    String productName;
    String entityId;
    String debitLimit;
    String creditLimit;
    String maxBalance;
    String debitAllowed;
    String creditAllowed;
    String dailyLimit;
    String minBalance;
    String status;
    String makerId;
    String makerDate;
    String approverId;
    String approverDate;
    String modifiedBy;
    String dateModified;
}
