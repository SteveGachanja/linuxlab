package com.coop.mwalletapi.customerslibrary.dao.model;
import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;
/**
 * @author okahia
 */

@Data
public class CreateCustomerDbResp  extends ResponseHeader{
    String customerNumber;
    String accountId;
    String accountNumber;
}
