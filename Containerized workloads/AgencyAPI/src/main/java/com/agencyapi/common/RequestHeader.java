package com.agencyapi.common;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class RequestHeader {
    String messageId;
    String requestDateTime;
    String requestType;
    String channelId;
}
