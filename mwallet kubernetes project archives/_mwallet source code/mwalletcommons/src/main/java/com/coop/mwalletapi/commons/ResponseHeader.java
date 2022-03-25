package com.coop.mwalletapi.commons;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class ResponseHeader {
    int responseCode;
    String responseMessage;
    String messageId;
}
