package com.agencyapi.common;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class ResponseHeader {
    int responseCode;
    String responseMessage;
    String messageId;
}
