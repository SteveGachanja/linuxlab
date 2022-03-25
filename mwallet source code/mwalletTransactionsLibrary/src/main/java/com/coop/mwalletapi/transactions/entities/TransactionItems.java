package com.coop.mwalletapi.transactions.entities;

import java.util.ArrayList;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class TransactionItems {
    ArrayList < TransactionItem > transactionItem = new ArrayList < TransactionItem > ();
    
}
