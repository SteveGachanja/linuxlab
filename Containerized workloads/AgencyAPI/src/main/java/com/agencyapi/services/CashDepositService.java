package com.agencyapi.services;

import com.agencyapi.dao.Agents;
import com.agencyapi.dao.TerminalUsers;
import com.agencyapi.dao.Terminals;
import com.agencyapi.dao.Transactions;
import com.agencyapi.entities.cashdep.CashDepositData;
import com.agencyapi.entities.cashdep.CashDepositDataResp;
import com.agencyapi.entities.cashdep.CashDepositReq;
import com.agencyapi.entities.cashdep.CashDepositResp;
import com.agencyapi.entities.cashdep.CashDepositValidateResp;
import com.agencyapi.entities.ft.TransactionItems;
import com.agencyapi.functions.Functions;
import com.agencyapi.repositories.AgentsRepository;
import com.agencyapi.repositories.TerminalUsersRepository;
import com.agencyapi.repositories.TerminalsRepository;
import com.agencyapi.repositories.TransactionsRepository;
import com.agencyapi.services.soa.AccountNumberValidationService;
import com.agencyapi.services.soa.FundsTransferService;
import com.agencyapi.services.soa.dto.AccountDetailsResponseDTO;
import com.agencyapi.services.soa.dto.FundsTransferResponseDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author okahia
 */

@Service
public class CashDepositService {

    @Autowired
    private TerminalUsersRepository tuRepository;

    @Autowired
    private TerminalsRepository tRepository;

    @Autowired
    private AccountNumberValidationService AcNoVS;

    @Autowired
    private AgentsRepository aRepository;

    @Autowired
    private FundsTransferService fts;

    @Autowired
    private TransactionsRepository tls;

    @Autowired
    private Functions func;

    public CashDepositValidateResp cashDepositValidate(CashDepositReq terminalReq) throws Exception {
        CashDepositValidateResp terminalResp = new CashDepositValidateResp();
        String AcNoEntered = terminalReq.getRequestBody().getMessageBody().getAcNo();
        String AmountEnteredStr = terminalReq.getRequestBody().getMessageBody().getAmount();
        double AmountEntered = Double.parseDouble(AmountEnteredStr);
        String NarrEntered = terminalReq.getRequestBody().getMessageBody().getNarration();
        String terminalId = terminalReq.getRequestBody().getMessageHeader().getTerminalId();
        String userName = terminalReq.getRequestBody().getMessageHeader().getUserName();
        
        Date postingDate = new Date();

        //getTerminal Details
        TerminalUsers tu = tuRepository.findByTerminalIdAndUserName(terminalId, userName);
        if (tu != null){
            if (!tu.getSessionToken().equalsIgnoreCase(terminalReq.getRequestBody().getMessageHeader().getSessionToken())){
                terminalResp.setResponseCode(1);
                terminalResp.setResponseMessage("Please login to proceed with this transaction");
                terminalResp.setMessageId(terminalReq.getMessageId());
                terminalResp.setResponseBody(Collections.emptyList());
                return terminalResp;
            }
        } else {
            terminalResp.setResponseCode(1);
            terminalResp.setResponseMessage("Terminal Data Not Found");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(Collections.emptyList());
            return terminalResp;
        }
        
        //validateAccount
        AccountDetailsResponseDTO validateAccountNumber = AcNoVS.validateAccountNumber(AcNoEntered);
        if (validateAccountNumber != null) {
            if (validateAccountNumber.getStatusDescription().equalsIgnoreCase("Success")) {
                //validate account details found
                if (validateAccountNumber.getAccStatus().equals("XXXXX")) {
                    terminalResp.setResponseCode(1);
                    terminalResp.setResponseMessage("Account " + AcNoEntered + " Is Not Allowed To Transact Status " + validateAccountNumber.getAccStatus());
                    terminalResp.setMessageId(terminalReq.getMessageId());
                    terminalResp.setResponseBody(Collections.emptyList());
                } else {
                    String RefNo = func.generateReferenceNo();
                    CashDepositData cdd = new CashDepositData();
                    List<CashDepositData> cddl = new ArrayList<>();

                    cdd.setAcNo(AcNoEntered);
                    cdd.setAccName(validateAccountNumber.getAccountName());
                    cdd.setAccStatus(validateAccountNumber.getAccStatus());
                    cdd.setAmount(AmountEnteredStr);
                    cdd.setCurrencyCode(validateAccountNumber.getCurrencyCode());
                    cdd.setNarration(NarrEntered);
                    cdd.setReferenceNo(RefNo);
                    cddl.add(cdd);

                    Thread TransLog = new Thread() {
                        public void run() {
                            try {
                                //get the terminal data
                                Terminals t = tRepository.findByTerminalId(terminalId);
                                List<Transactions> transLst = new ArrayList();

                                //DR
                                Transactions tranDr = new Transactions();
                                tranDr.setAcNo(t.getAcNoFloat());
                                tranDr.setAgencyNo(t.getAgentNo());
                                tranDr.setBranchCode("");
                                tranDr.setCurrencyCode("");
                                tranDr.setDrcrFlag("D");
                                tranDr.setMessageId(terminalReq.getMessageId());
                                tranDr.setNarrative(NarrEntered);
                                tranDr.setPostingDate(postingDate);
                                tranDr.setReferenceNo(RefNo);
                                tranDr.setTerminalId(terminalId);
                                tranDr.setTranType("ACD");
                                tranDr.setTransAmount(AmountEntered);
                                tranDr.setTransactionId("");
                                tranDr.setValueDate(postingDate);
                                transLst.add(tranDr);

                                //CR
                                Transactions transCr = new Transactions();
                                transCr.setAcNo(AcNoEntered);
                                transCr.setAgencyNo(t.getAgentNo());
                                transCr.setBranchCode("");
                                transCr.setCurrencyCode("");
                                transCr.setDrcrFlag("C");
                                transCr.setMessageId(terminalReq.getMessageId());
                                transCr.setNarrative(NarrEntered);
                                transCr.setPostingDate(postingDate);
                                transCr.setReferenceNo(RefNo);
                                transCr.setTerminalId(terminalId);
                                transCr.setTranType("ACD");
                                transCr.setTransAmount(AmountEntered);
                                transCr.setTransactionId("");
                                transCr.setValueDate(postingDate);
                                transLst.add(transCr);

                                tls.saveAll(transLst);

                            } catch (Exception v) {

                            }
                        }
                    };
                    TransLog.start();

                    terminalResp.setResponseCode(0);
                    terminalResp.setResponseMessage("Success");
                    terminalResp.setMessageId(terminalReq.getMessageId());
                    terminalResp.setResponseBody(cddl);
                }
            } else {
                terminalResp.setResponseCode(1);
                terminalResp.setResponseMessage(validateAccountNumber.getMessageDescription());
                terminalResp.setMessageId(terminalReq.getMessageId());
                terminalResp.setResponseBody(Collections.emptyList());
            }
        } else {
            terminalResp.setResponseCode(1);
            terminalResp.setResponseMessage("Account " + AcNoEntered + " Not Found");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(Collections.emptyList());
        }

                
                

        return terminalResp;
    }



    public CashDepositResp cashDepositPost(CashDepositReq terminalReq) throws Exception {
        CashDepositResp terminalResp = new CashDepositResp();
        String AcNoEntered = terminalReq.getRequestBody().getMessageBody().getAcNo();
        String AmountEnteredStr = terminalReq.getRequestBody().getMessageBody().getAmount();
        double AmountEntered = Double.parseDouble(AmountEnteredStr);
        String NarrEntered = terminalReq.getRequestBody().getMessageBody().getNarration();
        String terminalId = terminalReq.getRequestBody().getMessageHeader().getTerminalId();
        String userName = terminalReq.getRequestBody().getMessageHeader().getUserName();
        
        Date postingDate = new Date();

        //getTerminal Details
        TerminalUsers tu = tuRepository.findByTerminalIdAndUserName(terminalId, userName);
        if (tu != null){
            if (!tu.getSessionToken().equalsIgnoreCase(terminalReq.getRequestBody().getMessageHeader().getSessionToken())){
                terminalResp.setResponseCode(1);
                terminalResp.setResponseMessage("Please login to proceed with this transaction");
                terminalResp.setMessageId(terminalReq.getMessageId());
                terminalResp.setResponseBody(Collections.emptyList());
                return terminalResp;
            }
        } else {
            terminalResp.setResponseCode(1);
            terminalResp.setResponseMessage("Terminal Data Not Found");
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(Collections.emptyList());
            return terminalResp;
        }
        
        String refNo = terminalReq.getRequestBody().getMessageBody().getReferenceNo();

        //get account details
        AccountDetailsResponseDTO getAcNo = AcNoVS.validateAccountNumber(AcNoEntered);

        //get the terminal data
        Terminals t = tRepository.findByTerminalId(terminalId);

        //get the agent data for this terminal
        Agents a = aRepository.findByAgencyId(t.getAgencyId());

        //check if previously posted
        List<Transactions> transLst = tls.findByTerminalIdAndReferenceNo(terminalId, refNo);
        if (!transLst.isEmpty()) {
            if (func.isNullOrEmpty(transLst.get(0).getTransactionId())) {
                //Other Details
                String CorrelationID = refNo;
                String TransactionCode = "2100";

                List<TransactionItems> transItms = new ArrayList<>();
                TransactionItems tiDr = new TransactionItems();
                TransactionItems tiCr = new TransactionItems();

                //Construct the posting details
                //--DR
                tiDr.setAccountNumber(t.getAcNoFloat());
                tiDr.setBaseEquivalent(AmountEnteredStr);
                tiDr.setDebitCreditFlag("D");
                tiDr.setNarrative(NarrEntered);
                tiDr.setSourceBranch(a.getBranchCode());
                tiDr.setTransactionAmount(AmountEnteredStr);
                tiDr.setTransactionCode(TransactionCode);
                tiDr.setTransactionCurrency(getAcNo.getCurrencyCode());
                tiDr.setTransactionItemKey(CorrelationID);
                tiDr.setTransactionReference(CorrelationID);
                transItms.add(tiDr);

                //--CR
                tiCr.setAccountNumber(AcNoEntered);
                tiCr.setBaseEquivalent(AmountEnteredStr);
                tiCr.setDebitCreditFlag("C");
                tiCr.setNarrative(NarrEntered);
                tiCr.setSourceBranch(getAcNo.getBranchCode());
                tiCr.setTransactionAmount(AmountEnteredStr);
                tiCr.setTransactionCode(TransactionCode);
                tiCr.setTransactionCurrency(getAcNo.getCurrencyCode());
                tiCr.setTransactionItemKey(CorrelationID);
                tiCr.setTransactionReference(CorrelationID);
                transItms.add(tiCr);

                //do the ft
                FundsTransferResponseDTO cashDepFt = fts.fundsTransfer(CorrelationID, transItms);

                if (cashDepFt != null) {
                    //Construct response
                    if (cashDepFt.getStatusDescription().equalsIgnoreCase("success")) {
                        CashDepositDataResp cdd = new CashDepositDataResp();
                        List<CashDepositDataResp> cddl = new ArrayList<>();

                        cdd.setAcNo(AcNoEntered);
                        cdd.setAmount(AmountEnteredStr);
                        cdd.setNarration(NarrEntered);
                        cdd.setReferenceNo(cashDepFt.getMessageReference());
                        cdd.setTransactionID(cashDepFt.getTransactionID());
                        cddl.add(cdd);

                        terminalResp.setResponseCode(0);
                        terminalResp.setResponseMessage("Success");
                        terminalResp.setMessageId(terminalReq.getMessageId());
                        terminalResp.setResponseBody(cddl);

                        //update transactions data
                        transLst.forEach(items -> {
                            items.setTransactionId(cashDepFt.getTransactionID());
                        });
                        tls.saveAll(transLst);

                    } else {
                        terminalResp.setResponseCode(1);
                        terminalResp.setResponseMessage("Failed");
                        terminalResp.setMessageId(terminalReq.getMessageId());
                        terminalResp.setResponseBody(null);
                    }
                } else {
                    terminalResp.setResponseCode(2);
                    terminalResp.setResponseMessage("Failed");
                    terminalResp.setMessageId(terminalReq.getMessageId());
                    terminalResp.setResponseBody(null);
                }
            } else {
                //already posted
                terminalResp.setResponseCode(1);
                terminalResp.setResponseMessage("Transaction with Reference No " + refNo + " alredy posted");
                terminalResp.setMessageId(terminalReq.getMessageId());
                terminalResp.setResponseBody(null);
            }
        } else {
            terminalResp.setResponseCode(1);
            terminalResp.setResponseMessage("Validate transactions first"); // check that you are sending the validated ref no
            terminalResp.setMessageId(terminalReq.getMessageId());
            terminalResp.setResponseBody(null);
        }



        return terminalResp;
    }
    
}
