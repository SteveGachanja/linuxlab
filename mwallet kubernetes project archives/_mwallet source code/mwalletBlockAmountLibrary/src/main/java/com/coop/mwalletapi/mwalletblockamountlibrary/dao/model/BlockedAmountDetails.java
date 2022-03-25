/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coop.mwalletapi.mwalletblockamountlibrary.dao.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *
 * @author cndirangu
 */
@Getter
@Setter
public class BlockedAmountDetails implements Serializable {
    String requestID;
    String entity_ID;
    String actionType;
    String accountNumber;
    String amount;
    String reason;
    String channelID;
    String blockDate;
    String externalReference;
    String active;
    String blockedBalanceBefore;
    String blockedBalanceAfter;
    String status;
    String makerID;
    String makerDate;
    String approverID;
    String approverDate;
    String recordDate;
//    public String getVALUEDATE(String VALUEDATE) {
//        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(VALUEDATE)), ZoneId.systemDefault());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
//        return sdf.format(dateTime);
//    }
}
