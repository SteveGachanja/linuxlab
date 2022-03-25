package com.coop.mwalletapi.chargeslibrary.entities;


import com.coop.mwalletapi.chargeslibrary.dao.model.ChargesEnquiryDataDbResponse;
import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cndirangu
 */

@Getter
@Setter
public class ChargesEnquiryResponse extends ResponseHeader {
    ChargesEnquiryDataDbResponse responseBody;
    
}
