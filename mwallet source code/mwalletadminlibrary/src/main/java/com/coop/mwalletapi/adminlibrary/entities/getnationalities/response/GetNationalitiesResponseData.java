package com.coop.mwalletapi.adminlibrary.entities.getnationalities.response;

import com.coop.mwalletapi.adminlibrary.dao.model.GetNationalitiesDataDb;
import java.util.List;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetNationalitiesResponseData {
    List<GetNationalitiesDataDb> nationalities;
}
