/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coop.mwalletapi.customerslibrary.entities.customeradditionalwallet;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class RequestBody {
    String productCode;
    String documentNumber;
    String makerUserId;
    String makerDateTime;
    String approverUserId;
    String approverDateTime;
	String branchCode;
}
