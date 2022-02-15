package com.coop.mwalletapi.customer.get.allaccounts.req;

import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class RequestBody {
    String documentNo;
    String mobileNo;
    String customerNo;
}
