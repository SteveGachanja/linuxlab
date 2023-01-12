package com.agencyapi.dao;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Entity
@Data
@Table(name = "AGENTS")
public class Agents implements Serializable {
    @Id @GeneratedValue
    @Column(name="ID")
    private int id;

    @Column(name="AGENCY_ID")
    private String agencyId;

    @Column(name="DOCUMENT_TYPE")
    private String documentType;

    @Column(name="DOCUMENT_NO")
    private String documentNo;

    @Column(name="MOBILE_NO")
    private String mobileNo;

    @Column(name="FIRST_NAME")
    private String firstName;

    @Column(name="MIDDLE_NAME")
    private String middleName;

    @Column(name="LAST_NAME")
    private String lastName;

    @Column(name="EMAIL_ADDRESS")
    private String emailAddress;

    @Column(name="ADDRESS")
    private String address;

    @Column(name="GEO_LOCATION")
    private String geoLocation;

    @Column(name="BRANCH_CODE")
    private String branchCode;

    @Column(name="ACC_OFFICER_CODE")
    private String accountOfficerCode;

    @Column(name="SALE_OFFICER_CODE")
    private String saleOfficerCode;

    @Column(name="SEGMENT_ID")
    private int segmentId;

    @Column(name="STATUS")
    private int status;

    @Column(name="STATUS_CHANGE_DATE")
    Date statusChangeDate;

    @Column(name="MAKER_ID")
    private String makerId;

    @Column(name="MAKER_DATE")
    Date makerDate;

    @Column(name="APPROVER_ID")
    private String approverId;

    @Column(name="APPROVER_DATE")
    Date approverDate;

    @Column(name="MODIFIED_BY")
    private String modifiedBy;

    @Column(name="DATE_MODIFIED")
    Date dateModified;

}