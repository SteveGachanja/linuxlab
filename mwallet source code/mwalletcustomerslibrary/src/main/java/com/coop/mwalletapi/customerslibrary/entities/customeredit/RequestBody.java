package com.coop.mwalletapi.customerslibrary.entities.customeredit;

import lombok.Data;

/**
 * @author okahia
 */
@Data

public class RequestBody {
    String customerId;
    String customerNumber;
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
    String branchCode;
    String accountOfficerCode;
    String salesOfficerCode;
    String segmentId;
    String status;
    String modifiedBy;
    String nationality;
}
