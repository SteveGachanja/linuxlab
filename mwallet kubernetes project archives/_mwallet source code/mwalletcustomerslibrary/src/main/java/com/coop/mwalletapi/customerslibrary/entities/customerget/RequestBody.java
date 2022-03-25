package com.coop.mwalletapi.customerslibrary.entities.customerget;

import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class RequestBody {
    String documentType;
    String documentNo;
    String mobileNo;
    String customerNo;
}
