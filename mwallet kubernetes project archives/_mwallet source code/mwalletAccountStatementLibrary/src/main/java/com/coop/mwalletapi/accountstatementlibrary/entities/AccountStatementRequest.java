package com.coop.mwalletapi.accountstatementlibrary.entities;


import com.coop.mwalletapi.commons.RequestHeader;
import lombok.*;

/**
 * @author pkingongo
 */

@Data
public class AccountStatementRequest extends RequestHeader {
   
   RequestBody requestBody;
    
   
}
