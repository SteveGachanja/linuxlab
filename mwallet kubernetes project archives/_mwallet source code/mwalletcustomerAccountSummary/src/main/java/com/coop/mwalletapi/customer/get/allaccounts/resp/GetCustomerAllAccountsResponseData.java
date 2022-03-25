package com.coop.mwalletapi.customer.get.allaccounts.resp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class GetCustomerAllAccountsResponseData {
    String customerName;
    String documentNo;
    String mobileNo;
    String customerNo;
    List<GetCustomerAllAccountsResp> accountsData;
}
