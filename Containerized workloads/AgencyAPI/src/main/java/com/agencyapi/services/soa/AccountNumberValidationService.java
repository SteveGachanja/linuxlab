package com.agencyapi.services.soa;

import com.agencyapi.services.soa.dto.AccountDetailsResponseDTO;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class AccountNumberValidationService {

    @Value("${soa.getaccountdetails.url}")
    String soaUrl;

    @Value("${soa.credentials.username}")
    String soaUsername;

    @Value("${soa.credentials.password}")
    String soaPassword;

    public AccountDetailsResponseDTO validateAccountNumber(String accountNumber) {
        UUID uuid = UUID.randomUUID();
        ResponseEntity<String> acctLinkResponse = new ResponseEntity<>(HttpStatus.OK);
        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        AccountDetailsResponseDTO accnts = new AccountDetailsResponseDTO();

        accnts.setStatusCode("E_000");
        accnts.setStatusDescription("Fail");
        accnts.setMessageDescription("");
        accnts.setAccountNumber(accountNumber);

        try {

            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mes=\"urn://co-opbank.co.ke/CommonServices/Data/Message/MessageHeader\" xmlns:com=\"urn://co-opbank.co.ke/CommonServices/Data/Common\" xmlns:bsac=\"urn://co-opbank.co.ke/BS/Account/BSAccountDetails.3.0\">\n"
                    + "   <soapenv:Header>\n"
                    + "      <mes:RequestHeader>\n"
                    + "         <!--Optional:-->\n"
                    + "         <com:CreationTimestamp>" + sdf.format(currentTime) + "</com:CreationTimestamp> "
                    + "         <!--Optional:-->\n"
                    + "         <com:CorrelationID>" + UUID.randomUUID().toString() + "</com:CorrelationID> "
                    + "         <mes:MessageID>" + UUID.randomUUID().toString() + "</mes:MessageID>  "
                    + "         <mes:Credentials>\n"
                    + "            <mes:SystemCode>010</mes:SystemCode>           \n"
                    + "         </mes:Credentials>\n"
                    + "      </mes:RequestHeader>\n"
                    + "   </soapenv:Header>\n"
                    + "   <soapenv:Body>\n"
                    + "      <bsac:AccountDetailsRequest>\n"
                    + "         <bsac:AccountNumber>" + accountNumber + "</bsac:AccountNumber>\n"
                    + "      </bsac:AccountDetailsRequest>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";

            RestTemplate restTemplate = new RestTemplate();

            String url = soaUrl;

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(soaUsername, soaPassword);
            headers.add("SOAPAction", "\"GetAccountDetails\"");
            headers.add("Content-Type", "\"text/xml;charset=UTF-8\"");
            headers.setContentType(MediaType.TEXT_XML);

            HttpEntity<String> entity = new HttpEntity<String>(xml, headers);
            try {
                acctLinkResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            } catch (Exception ex) {
                acctLinkResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                accnts.setStatusCode("E_000");
                accnts.setStatusDescription("Fail");
                accnts.setMessageDescription(ex.toString());
            }
            if (acctLinkResponse.getStatusCode() == HttpStatus.OK) {
                accnts = extractDetailsFromSOAServiceResponse(acctLinkResponse.getBody());
            }
        } catch (Exception ex) {
            acctLinkResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            accnts.setStatusCode("E_000");
            accnts.setStatusDescription("Fail");
            accnts.setMessageDescription(ex.toString());
        }
        
        return accnts;
            
    }

    public AccountDetailsResponseDTO extractDetailsFromSOAServiceResponse(String responseBody) {
        AccountDetailsResponseDTO accnts = new AccountDetailsResponseDTO();
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
            String _email = "";
            String _phone = "";
            String _customerId = "";
            String _errorFlag = "";
            String _branchCode = "";
            String _currency = "";
            String _errorDesc = "";
            String validationStatus = "Y";
            String _availableBalance = "";
            String _accstatus = "";
            String _acccurrency = "";

            int recCnt = 0;

            NodeList soapEnvNodes = doc.getElementsByTagName("soapenv:Envelope");
            if (soapEnvNodes.getLength() > 0) {
                Element envElement = (Element) soapEnvNodes.item(0);

                NodeList headerNodes = envElement.getElementsByTagName("soapenv:Header");
                if (headerNodes.getLength() > 0) {
                    Element headerElement = (Element) headerNodes.item(0);
                    NodeList tnsNodes = headerElement.getElementsByTagName("head:ResponseHeader");
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
                        accnts.setStatusCode("E_000");
                        accnts.setStatusDescription("Fail");
                        accnts.setMessageDescription(_msgDesc);
                    } catch (Exception ex) {
                        accnts.setStatusCode("E_000");
                        accnts.setStatusDescription("Fail");
                        accnts.setMessageDescription("Error Occured ");
                    }
                } else if (_status.equals("S_001")) {
                    NodeList bodyNodes = envElement.getElementsByTagName("soapenv:Body");
                    if (bodyNodes.getLength() > 0) {
                        Element bodyElement = (Element) bodyNodes.item(0);
                        NodeList dataOutputNodes = bodyElement.getElementsByTagName("tns25:AccountDetailsResponse");
                        if (dataOutputNodes.getLength() > 0) {
                            Element acctDetailsElement = (Element) dataOutputNodes.item(0);
                            NodeList accountNumberNodes = acctDetailsElement.getElementsByTagName("tns25:AccountNumber");
                            int accnumlen = accountNumberNodes.getLength();
                            while/*if*/ (/*accountNumberNodes.getLength()*/accnumlen > 0) {
                                recCnt++;
                                accnumlen--;
                                _accountNumber = accountNumberNodes.item(accnumlen).getTextContent().trim();
                                //  System.out.println("_accountNumber=="+_accountNumber);
                            }

                            NodeList accountNameNodes = acctDetailsElement.getElementsByTagName("tns25:AccountName");
                            int accnamelen = accountNameNodes.getLength();
                            while/*if*/ (/*accountNumberNodes.getLength()*/accnamelen > 0) {
                                accnamelen--;
                                _customerName = accountNameNodes.item(accnamelen).getTextContent().trim();
                                //  System.out.println("_accountNumber=="+_accountNumber);
                            }

                            NodeList branchNameNodes = acctDetailsElement.getElementsByTagName("tns25:BranchName");
                            int brnnamelen = accountNameNodes.getLength();
                            while/*if*/ (/*accountNumberNodes.getLength()*/brnnamelen > 0) {
                                brnnamelen--;
                                _branchName = branchNameNodes.item(brnnamelen).getTextContent().trim();
                                //  System.out.println("_accountNumber=="+_accountNumber);
                            }

                            NodeList emailNodes = acctDetailsElement.getElementsByTagName("tns25:Email");
                            int emaillen = emailNodes.getLength();
                            while/*if*/ (/*emailNodes.getLength()*/emaillen > 0) {
                                emaillen--;
                                _email = emailNodes.item(emaillen).getTextContent().trim();
                            }
                            NodeList phoneNumberNodes = acctDetailsElement.getElementsByTagName("tns25:PhoneNumber");
                            int phonenumberlen = phoneNumberNodes.getLength();
                            while/*if*/ (/*phoneNumberNodes.getLength()*/phonenumberlen > 0) {
                                phonenumberlen--;
                                _phone = phoneNumberNodes.item(phonenumberlen).getTextContent().trim();
                            }
                            NodeList customerCodeNodes = acctDetailsElement.getElementsByTagName("tns25:CustomerCode");
                            int customerCodelen = customerCodeNodes.getLength();
                            while/*if*/ (/*customerCodeNodes.getLength()*/customerCodelen > 0) {
                                customerCodelen--;
                                _customerId = customerCodeNodes.item(customerCodelen).getTextContent().trim();
                            }

                            NodeList availableBalanceNodes = acctDetailsElement.getElementsByTagName("tns25:AvailableBalance");
                            int availballen = availableBalanceNodes.getLength();
                            while/*if*/ (/*availableBalanceNodes.getLength()*/availballen > 0) {
                                availballen--;
                                _availableBalance = availableBalanceNodes.item(availballen).getTextContent().trim();
                            }

                            NodeList statusNodes = acctDetailsElement.getElementsByTagName("tns25:Status");
                            int statslen = statusNodes.getLength();
                            while/*if*/ (/*statusNodes.getLength()*/statslen > 0) {
                                statslen--;
                                _accstatus = statusNodes.item(statslen).getTextContent().trim();
                            }

                            NodeList currNodes = acctDetailsElement.getElementsByTagName("tns25:CurrencyCode");
                            int currlen = currNodes.getLength();
                            while/*if*/ (/*currNodes.getLength()*/currlen > 0) {
                                currlen--;
                                _acccurrency = currNodes.item(currlen).getTextContent().trim();
                            }

                            NodeList brCodeNodes = acctDetailsElement.getElementsByTagName("tns25:BranchCode");
                            int brCodelen = brCodeNodes.getLength();
                            while/*if*/ (/*brCode.getLength()*/brCodelen > 0) {
                                brCodelen--;
                                _branchCode = brCodeNodes.item(brCodelen).getTextContent().trim();
                            }

                            accnts.setAccountNumber(_accountNumber);
                            accnts.setBranchName(_branchName);
                            accnts.setAccountName(_customerName);
                            accnts.setEmail(_email);
                            accnts.setPhoneNumber(_phone);
                            accnts.setCustomerID(_customerId);
                            accnts.setAvailableBalance(_availableBalance);
                            accnts.setAccStatus(_accstatus);
                            accnts.setCurrencyCode(_acccurrency);
                            accnts.setBranchCode(_branchCode);

                            if (recCnt > 0) {
                                accnts.setStatusCode("S_001");
                                accnts.setStatusDescription("Success");
                                accnts.setMessageDescription(_msgDesc);
                            }

                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            accnts.setStatusCode("E_000");
            accnts.setStatusDescription("Fail");
            accnts.setMessageDescription("Error Occured " + e.toString());
        }
        return accnts;
    }

}
