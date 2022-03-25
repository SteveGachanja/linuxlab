package com.coop.mwalletapi.adminlibrary.entities.getapiuser.response;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetApiUsersResponse extends ResponseHeader {
    GetApiUsersResponseData responseBody;
}
