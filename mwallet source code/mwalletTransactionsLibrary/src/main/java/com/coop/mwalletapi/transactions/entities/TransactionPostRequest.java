package com.coop.mwalletapi.transactions.entities;

import com.coop.mwalletapi.transactions.entities.req.RequestBody;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class TransactionPostRequest {
    private String apiUser;
    private String apiPassword;
    private String messageId;
    private String requestDateTime;
    private String requestType;
    private String channelId;
    RequestBody requestBody;
}
