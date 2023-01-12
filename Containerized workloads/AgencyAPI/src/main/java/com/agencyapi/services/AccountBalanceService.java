package com.agencyapi.services;

import com.agencyapi.entities.acc.AccountBalanceData;
import com.agencyapi.entities.acc.AccountBalanceReq;
import com.agencyapi.entities.acc.AccountBalanceResp;
import com.agencyapi.services.soa.AccountBalanceGet;
import com.agencyapi.services.soa.dto.AccountBalanceResponseDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author okahia
 */

@Service
public class AccountBalanceService {

    @Autowired
    private AccountBalanceGet accbal;

    public AccountBalanceResp getAccountBalance(AccountBalanceReq terminalReq) throws Exception {
        AccountBalanceResp acbalResp = new AccountBalanceResp();
        String AcNoEntered = terminalReq.getRequestBody().getAcNo();

        try {
            AccountBalanceResponseDTO accBal = accbal.getAccountBalance(AcNoEntered);
            if (accBal == null) {
                acbalResp.setResponseCode(1);
                acbalResp.setResponseMessage("No Data");
                acbalResp.setMessageId(terminalReq.getMessageId());
                acbalResp.setResponseBody(Collections.emptyList());
                return acbalResp;
            } else {
                List<AccountBalanceData> tvdl = new ArrayList();
                AccountBalanceData tvd = new AccountBalanceData();

                tvd.setAcNo(accBal.getAccountNumber());
                tvd.setAcName(accBal.getAccountName());
                tvd.setAcBalAvail(accBal.getAvailableBalance());
                tvdl.add(tvd);

                acbalResp.setResponseCode(0);
                acbalResp.setResponseMessage("Success");
                acbalResp.setMessageId(terminalReq.getMessageId());
                acbalResp.setResponseBody(tvdl);
            }

        } catch (Exception ex) {

        }

        return acbalResp;
    }
    
}
