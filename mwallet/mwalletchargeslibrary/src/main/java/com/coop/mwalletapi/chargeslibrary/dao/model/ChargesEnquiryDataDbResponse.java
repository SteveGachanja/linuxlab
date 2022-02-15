package com.coop.mwalletapi.chargeslibrary.dao.model;


import com.coop.mwalletapi.chargeslibrary.entities.ChargeDetails;
import com.coop.mwalletapi.commons.ResponseHeader;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cndirangu
 */

@Getter
@Setter
public class ChargesEnquiryDataDbResponse{
    String transactionCode;
    String cbsGLAccount;
    String walletGl;
    int creditAccountRequired;
    double transactionAmount;
    List<ChargeDetails> chargeDetails = new ArrayList<>();
    
}
