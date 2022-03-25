package com.coop.mwalletapi.customerslibrary.entities.customercreate;

import lombok.Data;

/**
 * @author okahia
 */
@Data

public class RequestBody {
    String productCode;
    String currency;
    String branchCode;
    String documentType;
    String documentNumber;
    String mobileNumber;
    String emailAddress;
    String title;
    String firstName;
    String middleName;
    String lastName;
    String dateOfBirth;
    String gender;
    String nationality;
    String accountOfficerCode;
    String salesOfficerCode;
    int segmentId;
    String makerUserId;
    String makerDateTime;
    String approverUserId;
    String approverDateTime;
}
