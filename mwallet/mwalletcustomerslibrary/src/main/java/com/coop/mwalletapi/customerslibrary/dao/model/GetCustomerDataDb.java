package com.coop.mwalletapi.customerslibrary.dao.model;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetCustomerDataDb {   
    String customerId;
    String customerNumber;
    String documentType;
    String documentNo;
    String mobileNo;
    String emailAddress;
    String title;
    String firstName;
    String lastName;
    String middleName;
    String dob;
    String gender;
    String branchCode;
    String accofficerCode;
    String saleofficerCode;
    String segmentId;
    String segmentCode;
    String segmentName;
    String statusCode;
    String Status;
    String statusChangeDate;
    String makerId;
    String makerDate;
    String approverId;
    String approverDate;
    String modifiedBy;
    String dateModified;
    String nationalityId;
    String nationality;
    String nationalityCode;
}
