package com.coop.mwalletapi.adminlibrary.dao.model;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class GetNationalitiesDataDb {
    String nationalityId;
    String countryName;
    String nationality;
    String status;
    String alpha3Code;
    String alpha2Code;
    String countryCode;
}
