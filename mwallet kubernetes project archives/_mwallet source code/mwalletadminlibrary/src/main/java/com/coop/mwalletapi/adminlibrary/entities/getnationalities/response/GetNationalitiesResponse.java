package com.coop.mwalletapi.adminlibrary.entities.getnationalities.response;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetNationalitiesResponse extends ResponseHeader {
    GetNationalitiesResponseData responseBody;
}
