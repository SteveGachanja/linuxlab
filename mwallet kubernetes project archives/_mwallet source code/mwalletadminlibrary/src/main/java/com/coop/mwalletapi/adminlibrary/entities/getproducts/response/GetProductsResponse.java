package com.coop.mwalletapi.adminlibrary.entities.getproducts.response;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetProductsResponse extends ResponseHeader {
    GetProductsResponseData responseBody;
}
