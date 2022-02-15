package com.coop.mwalletapi.accountstatementlibrary.entities.v3;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class RequestBody {
    String accountAlias;
    int maxRows;
    int pageNumber;
    String transactionId;
    String startDate;
    String endDate;
}
