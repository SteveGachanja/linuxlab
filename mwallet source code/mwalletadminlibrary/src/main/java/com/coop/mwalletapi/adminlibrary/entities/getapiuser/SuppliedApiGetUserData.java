package com.coop.mwalletapi.adminlibrary.entities.getapiuser;
import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author okahia
 */
@Data
public class SuppliedApiGetUserData extends RequestHeader {
    RequestBody requestBody;
}
