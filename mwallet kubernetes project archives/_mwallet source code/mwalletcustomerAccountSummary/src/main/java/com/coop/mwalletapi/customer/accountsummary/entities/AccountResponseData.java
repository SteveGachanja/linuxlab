package com.coop.mwalletapi.customer.accountsummary.entities;
import com.coop.mwalletapi.customer.accountsummary.dao.model.AccountDataDbDao;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author okahia
 */

@Getter
@Setter
public class AccountResponseData {
    List<AccountDataDbDao> accountData;
}
