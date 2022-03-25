package com.coop.mwalletapi.adminlibrary.entities.getproducts;

import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class SuppliedApiGetProductsData extends RequestHeader{
    RequestBody requestBody;
}
