package com.coop.mwalletapi.adminlibrary.dao.model;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetSegmentsDataDb {
    String segmentId;
    String segmentCode;
    String segmentName;
    String entityId;
    String status;
    String makerId;
    String makerDate;
    String approverId;
    String approverDate;
    String modifiedBy;
    String dateModified;
}

