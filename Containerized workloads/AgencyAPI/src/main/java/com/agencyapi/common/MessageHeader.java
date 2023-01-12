package com.agencyapi.common;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class MessageHeader {
    String terminalId;
    String userName;
    String sessionToken;
    String postType;
}
