package com.agencyapi.services.soa.dto;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class ResponseHeader {
    String statusCode; //E_000
    String statusDescription;//Failed
    String messageDescription;//The account does not exist
}
