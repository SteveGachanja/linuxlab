package com.agencyapi.entities.admin;

import com.agencyapi.common.RequestHeader;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class TerminalTypeByTerminalIdReq  extends RequestHeader{
    RequestBodyByTerminalId requestBody;
}
