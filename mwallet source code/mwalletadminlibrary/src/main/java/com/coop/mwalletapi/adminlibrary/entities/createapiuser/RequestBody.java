package com.coop.mwalletapi.adminlibrary.entities.createapiuser;

import lombok.Data;

/**
 * @author okahia
 */
@Data
public class RequestBody {
    int entityId;
    String userName;
    String password;
}
