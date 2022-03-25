package com.coop.mwalletapi.chargeslibrary.entities;


import com.coop.mwalletapi.commons.RequestHeader;
import lombok.*;

/**
 * @author pkingongo
 */

@Data
public class InputChargesRequest extends RequestHeader {
   
   RequestBody requestBody;
    
   
}
