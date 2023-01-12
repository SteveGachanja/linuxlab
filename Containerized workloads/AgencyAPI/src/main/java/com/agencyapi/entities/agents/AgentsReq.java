package com.agencyapi.entities.agents;

import com.agencyapi.common.RequestHeader;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class AgentsReq  extends RequestHeader{
    RequestBody requestBody;
}
