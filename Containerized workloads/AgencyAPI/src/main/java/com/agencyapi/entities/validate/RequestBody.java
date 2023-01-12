package com.agencyapi.entities.validate;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class RequestBody {
    String terminalId;
    String pinNo;
    String userName;
    String password;
}
