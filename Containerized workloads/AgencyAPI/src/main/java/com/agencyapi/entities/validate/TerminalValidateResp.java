package com.agencyapi.entities.validate;

import com.agencyapi.common.ResponseHeader;
import java.util.List;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class TerminalValidateResp  extends ResponseHeader{
    List<TerminalValidateData> responseBody;
}
