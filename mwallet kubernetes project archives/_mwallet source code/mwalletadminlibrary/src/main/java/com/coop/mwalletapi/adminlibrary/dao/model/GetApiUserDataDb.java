package com.coop.mwalletapi.adminlibrary.dao.model;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetApiUserDataDb {
    String userId;
    String userName;
    String entityId;
    String password;
    String status;
    String createdBy;
    String dateCreated;
}
