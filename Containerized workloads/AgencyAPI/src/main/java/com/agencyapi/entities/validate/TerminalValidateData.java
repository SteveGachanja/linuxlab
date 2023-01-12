package com.agencyapi.entities.validate;

import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class TerminalValidateData {
    String agencyId;
    String mobileNo;
    String sessionToken;
    String agentNo;
    String agentName;
    String agentAcNo;
    int userType;
    int terminalType;
    int status;
    String appVersion;
}
