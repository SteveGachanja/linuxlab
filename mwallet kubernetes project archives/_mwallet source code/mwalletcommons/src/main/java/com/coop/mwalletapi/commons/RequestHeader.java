package com.coop.mwalletapi.commons;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class RequestHeader {
    String apiUser;
    String apiPassword;
    String messageId;
    String requestDateTime;
    String requestType;
    String channelId;
}
