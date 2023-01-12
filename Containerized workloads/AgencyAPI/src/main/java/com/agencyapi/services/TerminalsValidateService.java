package com.agencyapi.services;

import com.agencyapi.dao.Agents;
import com.agencyapi.dao.TerminalUsers;
import com.agencyapi.dao.Terminals;
import com.agencyapi.entities.validate.TerminalValidateData;
import com.agencyapi.entities.validate.TerminalValidateReq;
import com.agencyapi.entities.validate.TerminalValidateResp;
import com.agencyapi.functions.Enc;
import com.agencyapi.functions.Functions;
import com.agencyapi.repositories.AgentsRepository;
import com.agencyapi.repositories.TerminalUsersRepository;
import com.agencyapi.repositories.TerminalsRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author okahia
 */
@Service
public class TerminalsValidateService {

    @Autowired
    private TerminalsRepository tRepository;

    @Autowired
    private TerminalUsersRepository tuRepository;

    @Autowired
    private AgentsRepository aRepository;

    @Autowired
    Enc enc;

    @Autowired
    Functions func;

    public TerminalValidateResp validateTerminal(TerminalValidateReq terminalReq) throws Exception {
        TerminalValidateResp terminalResp = new TerminalValidateResp();
        List<TerminalValidateData> tt = new ArrayList<>();
        String sessionToken = "";

        try {
            Terminals t = tRepository.findByTerminalId(terminalReq.getRequestBody().getTerminalId());

            if (t == null) {
                terminalResp.setResponseCode(1);
                terminalResp.setResponseMessage("No Data");
                terminalResp.setMessageId(terminalReq.getMessageId());
                terminalResp.setResponseBody(Collections.emptyList());
                return terminalResp;
            } else {
                TerminalUsers tu = null;

                if (t.getStatus() == 1) {
                    List<TerminalUsers> tul = tuRepository.findByTerminalId(terminalReq.getRequestBody().getTerminalId());
                    if (tul != null) {
                        if (tul.size() == 1) {
                            tu = tul.get(0);
                        }
                        if (tul.size() > 1) {

                            if (terminalReq.getRequestBody().getUserName() != null || 
                                !func.isNullOrEmpty(terminalReq.getRequestBody().getUserName())
                            ) {
                                List<TerminalUsers> tulTemp;

                                tulTemp = tul.stream()
                                    .filter(n-> n.getUserName().equalsIgnoreCase(terminalReq.getRequestBody().getUserName()))
                                    .collect(Collectors.toList());
                                if (!tulTemp.isEmpty()){
                                    tu = tulTemp.get(0);
                                } else {
                                    terminalResp.setResponseCode(2);
                                    terminalResp.setResponseMessage("Invalid Credentials");
                                    terminalResp.setMessageId(terminalReq.getMessageId());
                                    terminalResp.setResponseBody(Collections.emptyList());
                                    return terminalResp;
                                }
                            } else {
                                terminalResp.setResponseCode(2);
                                terminalResp.setResponseMessage("Provide Username and password");
                                terminalResp.setMessageId(terminalReq.getMessageId());
                                terminalResp.setResponseBody(Collections.emptyList());
                                return terminalResp;
                            }
                        }
                    }

                    if (tu != null) {
                        if (!func.isNullOrEmpty(terminalReq.getRequestBody().getPinNo()) || !func.isNullOrEmpty(terminalReq.getRequestBody().getPassword())) {
                            String login = "";
                            if (!func.isNullOrEmpty(terminalReq.getRequestBody().getPinNo())) {
                                if (enc.decodeAndDecrypt(tu.getPinNo()).equals(enc.decodeAndDecrypt(terminalReq.getRequestBody().getPinNo()))) {
                                    login = "1";
                                }
                            } else if (!func.isNullOrEmpty(terminalReq.getRequestBody().getPassword())) {
                                if (enc.decodeAndDecrypt(tu.getPassword()).equals(enc.decodeAndDecrypt(terminalReq.getRequestBody().getPassword()))) {
                                    login = "1";
                                }
                            }

                            if (login.equalsIgnoreCase("1")) {
                                sessionToken = enc.randomString(50);
                                tu.setSessionToken(sessionToken);
                                tuRepository.save(tu);

                                //get the agent details
                                Agents a = aRepository.findByAgencyId(t.getAgencyId());

                                TerminalValidateData tvd = new TerminalValidateData();
                                tvd.setAgencyId(t.getAgencyId());
                                tvd.setAgentNo(t.getAgentNo());
                                tvd.setMobileNo(t.getMobileNo());
                                tvd.setSessionToken(tu.getSessionToken());
                                tvd.setUserType(tu.getUserType());
                                tvd.setTerminalType(t.getTerminalType());
                                tvd.setStatus(tu.getStatus());
                                tvd.setAgentAcNo(t.getAcNoFloat());
                                tvd.setAgentName(a.getFirstName());
                                tvd.setAppVersion("v1.0.0");

                                tt.add(tvd);

                                terminalResp.setResponseCode(0);
                                terminalResp.setResponseMessage("Success");
                                terminalResp.setMessageId(terminalReq.getMessageId());
                                terminalResp.setResponseBody(tt);
                            } else {
                                terminalResp.setResponseCode(2);
                                terminalResp.setResponseMessage("Invalid Credentials");
                                terminalResp.setMessageId(terminalReq.getMessageId());
                                terminalResp.setResponseBody(Collections.emptyList());
                            }
                        } else {
                            terminalResp.setResponseCode(2);
                            terminalResp.setResponseMessage("Invalid Credentials");
                            terminalResp.setMessageId(terminalReq.getMessageId());
                            terminalResp.setResponseBody(Collections.emptyList());
                        }
                    } else {
                        terminalResp.setResponseCode(1);
                        terminalResp.setResponseMessage("No Data");
                        terminalResp.setMessageId(terminalReq.getMessageId());
                        terminalResp.setResponseBody(Collections.emptyList());
                    }

                } else {
                    terminalResp.setResponseCode(3);
                    terminalResp.setResponseMessage("Invalid Status");
                    terminalResp.setMessageId(terminalReq.getMessageId());
                    terminalResp.setResponseBody(Collections.emptyList());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return terminalResp;
    }

}
