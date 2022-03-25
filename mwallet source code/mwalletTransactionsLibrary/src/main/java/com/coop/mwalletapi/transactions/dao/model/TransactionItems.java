/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coop.mwalletapi.transactions.dao.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *
 * @author fosano
 */
@Getter
@Setter
public class TransactionItems implements Serializable {
    String transaction_ID;
    String tran_Item_ID;
    String transaction_Item_key;
    String tran_Code;
    String account;
    String dr_Cr_Flag;
    String transaction_Amount;
    String currency;
    String narrative;
    String base_Equivalent;
    String source_Branch;
    String date_Created;
    String account_No_Req;
    String transactionID;
    String posting_Date;
    String account_Number_Cbs;

}
