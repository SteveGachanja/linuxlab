package com.coop.mwalletapi.adminlibrary.entities.createapiuser.response;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class CreateApiUserResponse extends ResponseHeader {
    CreateApiUserResponseData responseBody;
}
