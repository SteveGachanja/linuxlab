package com.coop.mwalletapi.adminlibrary.entities.createapiuser;
import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author okahia
 */
@Data
public class SuppliedApiUserCreateData extends RequestHeader {
    RequestBody requestBody;
}
