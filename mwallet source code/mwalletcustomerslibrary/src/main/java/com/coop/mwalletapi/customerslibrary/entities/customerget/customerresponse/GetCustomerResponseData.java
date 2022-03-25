package com.coop.mwalletapi.customerslibrary.entities.customerget.customerresponse;
import com.coop.mwalletapi.customerslibrary.dao.model.GetCustomerDataDb;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class GetCustomerResponseData {
    List<GetCustomerDataDb> mCustData;
}
