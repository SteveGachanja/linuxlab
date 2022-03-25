package com.coop.mwalletapi.accountstatementlibrary.dao.model;


import com.coop.mwalletapi.accountstatementlibrary.entities.AccountStatement;
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
public class AccountStatementDataDbResponse{    
    List<AccountStatement> transactions = new ArrayList<>();
    
}
