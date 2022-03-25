/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coop.mwalletapi.chargeslibrary.entities;

import lombok.Data;

/**
 *
 * @author cndirangu
 */

@Data
public class RequestBody {
   String transactionCode;
   String productCode;
   String branchCode;
   double transactionAmount;  
}
