package com.agencyapi.entities;

import com.agencyapi.common.RequestHeader;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class TerminalUsersReq extends RequestHeader{
    RequestBody requestBody;    
}
