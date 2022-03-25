package com.coop.mwalletapi.adminlibrary.entities.getproducts.response;

import com.coop.mwalletapi.adminlibrary.dao.model.GetProductsDataDb;
import java.util.List;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetProductsResponseData {
    List<GetProductsDataDb> products;
}
