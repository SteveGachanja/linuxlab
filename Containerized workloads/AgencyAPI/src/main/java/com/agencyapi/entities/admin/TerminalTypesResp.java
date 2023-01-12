package com.agencyapi.entities.admin;

import com.agencyapi.common.ResponseHeader;
import com.agencyapi.dao.TerminalsTypes;
import java.util.List;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class TerminalTypesResp  extends ResponseHeader{
    List<TerminalsTypes> responseBody;
}
