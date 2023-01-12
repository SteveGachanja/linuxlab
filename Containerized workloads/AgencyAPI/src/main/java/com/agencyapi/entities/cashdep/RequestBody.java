package com.agencyapi.entities.cashdep;

import com.agencyapi.common.MessageHeader;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class RequestBody {
    MessageHeader messageHeader;
    MessageBody messageBody;
}
