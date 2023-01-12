package com.agencyapi.services.soa;

import com.agencyapi.services.soa.dto.AccountBalanceResponseDTO;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author okahia
 */

@Service
public class AccountBalanceGet {
    @Value("${soa.accountbalanceget.url}")
    String soaUrl;

    @Value("${soa.credentials.username}")
    String soaUsername;

    @Value("${soa.credentials.password}")
    String soaPassword;

    public AccountBalanceResponseDTO getAccountBalance(String accountNumber) {
        UUID uuid = UUID.randomUUID();
        ResponseEntity<String> acctLinkResponse = new ResponseEntity<>(HttpStatus.OK);
        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String requestDate = sdf.format(currentTime);
        
        AccountBalanceResponseDTO accbal = new AccountBalanceResponseDTO();
        accbal.setStatusCode("E_000");
        accbal.setStatusDescription("Fail");
        accbal.setMessageDescription("");
        accbal.setAccountNumber(accountNumber);

        try {

            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"urn://co-opbank.co.ke/CommonServices/BS/Account/AccountBalance/4.1\" xmlns:com=\"urn://co-opbank.co.ke/CommonServices/Data/Common\" xmlns:mes=\"urn://co-opbank.co.ke/CommonServices/Data/Message/MessageHeader\">\n"
            + "   <soapenv:Header>\n"
            + "      <ns:RequestHeader>\n"
            + "         <com:CreationTimestamp>" + requestDate + "</com:CreationTimestamp>\n"
            + "         <com:CorrelationID>" + uuid + "</com:CorrelationID>\n"
            + "         <mes:MessageID>" + uuid + "</mes:MessageID>\n"
            + "         <mes:Credentials>\n"
            + "            <mes:SystemCode>AGENCY</mes:SystemCode>\n"
            + "            <mes:Username>OMNI</mes:Username>\n"
            + "            <mes:Password>password</mes:Password>\n"
            + "            <mes:BankID>01</mes:BankID>\n"
            + "         </mes:Credentials>\n"
            + "      </ns:RequestHeader>\n"
            + "   </soapenv:Header>\n"
            + "   <soapenv:Body>\n"
            + "      <ns:DataInput>\n"
            + "         <OperationParameters>\n"
            + "            <BalanceType>Account</BalanceType>\n"
            + "            <AveragePeriod>0</AveragePeriod>\n"
            + "         </OperationParameters>\n"
            + "         <Account>\n"
            + "            <AccountNumber>" + accountNumber + "</AccountNumber>\n"
            + "         </Account>\n"
            + "      </ns:DataInput>\n"
            + "   </soapenv:Body>\n"
            + "</soapenv:Envelope>";

            RestTemplate restTemplate = new RestTemplate();

            String url = soaUrl;

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(soaUsername, soaPassword);
            headers.add("SOAPAction", "\"AccountBalance\"");
            headers.add("Content-Type", "\"text/xml;charset=UTF-8\"");
            headers.setContentType(MediaType.TEXT_XML);

            HttpEntity<String> entity = new HttpEntity<String>(xml, headers);
            try {
                acctLinkResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            } catch (Exception ex) {
                acctLinkResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                accbal.setStatusCode("E_000");
                accbal.setStatusDescription("Fail");
                accbal.setMessageDescription(ex.toString());
            }
            if (acctLinkResponse.getStatusCode() == HttpStatus.OK) {
                accbal = extractDetailsFromSOAServiceResponse(acctLinkResponse.getBody());
            }
        } catch (Exception ex) {
            acctLinkResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return accbal;
    }

    public AccountBalanceResponseDTO extractDetailsFromSOAServiceResponse(String responseBody) {
        AccountBalanceResponseDTO accbal = new AccountBalanceResponseDTO();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseBody));
            org.w3c.dom.Document doc = db.parse(is);

            String _status = "";
            String _msgDesc = "";
            String _accountNumber = "";
            String _customerName = "";
            String _branchName = "";
            String _branchCode = "";
            String _availableBalance = "";
            String _acccurrency = "";

            int recCnt = 0;

            NodeList soapEnvNodes = doc.getElementsByTagName("soapenv:Envelope");
            if (soapEnvNodes.getLength() > 0) {
                Element envElement = (Element) soapEnvNodes.item(0);

                NodeList headerNodes = envElement.getElementsByTagName("soapenv:Header");
                if (headerNodes.getLength() > 0) {
                    Element headerElement = (Element) headerNodes.item(0);
                    //NodeList tnsNodes = headerElement.getElementsByTagName("head:ResponseHeader");
                    NodeList tnsNodes = headerElement.getElementsByTagName("tns12:ReplyHeader");
                    if (tnsNodes.getLength() > 0) {
                        Element tnsReplyHeaderElement = (Element) tnsNodes.item(0);
                        NodeList statusNodes = tnsReplyHeaderElement.getElementsByTagName("head:StatusCode");
                        if (statusNodes.getLength() > 0) {
                            _status = statusNodes.item(0).getTextContent().trim();
                        }
                        //get message description
                        NodeList msgDescNodes = tnsReplyHeaderElement.getElementsByTagName("head:MessageDescription");
                        if (msgDescNodes.getLength() > 0) {
                            _msgDesc = msgDescNodes.item(0).getTextContent().trim();
                        }
                    }
                }
                if (_status.equals("E_000")) {
                    try {
                        accbal.setStatusCode("E_000");
                        accbal.setStatusDescription("Fail");
                        accbal.setMessageDescription(_msgDesc);
                    } catch (Exception ex) {
                        accbal.setStatusCode("E_000");
                        accbal.setStatusDescription("Fail");
                        accbal.setMessageDescription("Error Occured ");
                    }
                } else if (_status.equals("S_001")) {
                    NodeList bodyNodes = envElement.getElementsByTagName("soapenv:Body");
                    if (bodyNodes.getLength() > 0) {
                        Element bodyElement = (Element) bodyNodes.item(0);
                        NodeList dataOutputNodes = bodyElement.getElementsByTagName("tns12:DataOutput");
                        if (dataOutputNodes.getLength() > 0) {
                            Element acctDetailsElement = (Element) dataOutputNodes.item(0);
                            NodeList accountNumberNodes = acctDetailsElement.getElementsByTagName("AccountNumber");
                            int accnumlen = accountNumberNodes.getLength();
                            while/*if*/ (/*accountNumberNodes.getLength()*/accnumlen > 0) {
                                recCnt++;
                                accnumlen--;
                                _accountNumber = accountNumberNodes.item(accnumlen).getTextContent().trim();
                                //  System.out.println("_accountNumber=="+_accountNumber);
                            }

                            NodeList accountNameNodes = acctDetailsElement.getElementsByTagName("AccountName");
                            int accnamelen = accountNameNodes.getLength();
                            while/*if*/ (/*accountNumberNodes.getLength()*/accnamelen > 0) {
                                accnamelen--;
                                _customerName = accountNameNodes.item(accnamelen).getTextContent().trim();
                                //  System.out.println("_accountNumber=="+_accountNumber);
                            }

                            NodeList branchNameNodes = acctDetailsElement.getElementsByTagName("BranchName");
                            int brnnamelen = accountNameNodes.getLength();
                            while/*if*/ (/*accountNumberNodes.getLength()*/brnnamelen > 0) {
                                brnnamelen--;
                                _branchName = branchNameNodes.item(brnnamelen).getTextContent().trim();
                                //  System.out.println("_accountNumber=="+_accountNumber);
                            }

                            NodeList availableBalanceNodes = acctDetailsElement.getElementsByTagName("AvailableBalance");
                            int availballen = availableBalanceNodes.getLength();
                            while/*if*/ (/*availableBalanceNodes.getLength()*/availballen > 0) {
                                availballen--;
                                _availableBalance = availableBalanceNodes.item(availballen).getTextContent().trim();
                            }

                            NodeList currNodes = acctDetailsElement.getElementsByTagName("CurrencyCode");
                            int currlen = currNodes.getLength();
                            while/*if*/ (/*currNodes.getLength()*/currlen > 0) {
                                currlen--;
                                _acccurrency = currNodes.item(currlen).getTextContent().trim();
                            }

                            NodeList brCodeNodes = acctDetailsElement.getElementsByTagName("BranchSortCode");
                            int brCodelen = brCodeNodes.getLength();
                            while/*if*/ (/*brCode.getLength()*/brCodelen > 0) {
                                brCodelen--;
                                _branchCode = brCodeNodes.item(brCodelen).getTextContent().trim();
                            }

                            accbal.setAccountNumber(_accountNumber);
                            accbal.setAccountName(_customerName);
                            accbal.setBranchName(_branchName);
                            accbal.setAvailableBalance(_availableBalance);
                            accbal.setCurrencyCode(_acccurrency);
                            accbal.setBranchCode(_branchCode);

                            if (recCnt > 0) {
                                accbal.setStatusCode("S_001");
                                accbal.setStatusDescription("Success");
                                accbal.setMessageDescription(_msgDesc);
                            }

                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            accbal.setStatusCode("E_000");
            accbal.setStatusDescription("Fail");
            accbal.setMessageDescription("Error Occured " + e.toString());
        }
        return accbal;
    }

}
