package com.coop.mwalletapi.accountstatementlibrary.entities;


import com.coop.mwalletapi.accountstatementlibrary.dao.model.AccountStatementDataDbResponse;
import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Getter;
import lombok.Setter;

/**
 * @author cndirangu
 */

@Getter
@Setter
public class AccountStatementResponse extends ResponseHeader {
    AccountStatementDataDbResponse responseBody;
    
}
