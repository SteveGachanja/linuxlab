package com.agencyapi.entities.validate;

import com.agencyapi.common.RequestHeader;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class TerminalValidateReq  extends RequestHeader{
    RequestBody requestBody;
}
