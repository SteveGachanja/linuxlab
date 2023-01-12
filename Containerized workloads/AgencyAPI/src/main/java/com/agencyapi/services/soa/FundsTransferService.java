package com.agencyapi.services.soa;

import com.agencyapi.entities.ft.TransactionItems;
import com.agencyapi.services.soa.dto.FundsTransferResponseDTO;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
public class FundsTransferService {

    @Value("${soa.fundstransfersyncpost.url}")
    String soaUrl;

    @Value("${soa.credentials.username}")
    String soaUsername;

    @Value("${soa.credentials.password}")
    String soaPassword;

    public FundsTransferResponseDTO fundsTransfer(String CorrelationID, List<TransactionItems> ti) {
        UUID uuid = UUID.randomUUID();
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);

        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String requestDate = sdf.format(currentTime);

        SimpleDateFormat sdfv = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdfv.setTimeZone(TimeZone.getTimeZone("GMT"));
        String valueDate = sdfv.format(currentTime);

        FundsTransferResponseDTO ftResp = new FundsTransferResponseDTO();

        ftResp.setStatusCode("E_000");
        ftResp.setStatusDescription("Fail");
        ftResp.setMessageDescription("");

        try {
            String transactionItems = createTransactionItems(ti);

            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:mes=\"urn://co-opbank.co.ke/CommonServices/Data/Message/MessageHeader\" xmlns:com=\"urn://co-opbank.co.ke/CommonServices/Data/Common\" xmlns:ns=\"urn://co-opbank.co.ke/Banking/CanonicalDataModel/FundsTransfer/4.1\">\n"
            + "   <soapenv:Header>\n"
            + "      <mes:RequestHeader>\n"
            + "         <com:CreationTimestamp>" + requestDate + "</com:CreationTimestamp>\n"
            + "         <com:CorrelationID>" + CorrelationID + "</com:CorrelationID>\n"
            + "         <mes:FaultTO></mes:FaultTO>\n"
            + "         <mes:MessageID>" + CorrelationID + "</mes:MessageID>\n"
            + "         <mes:ReplyTO></mes:ReplyTO>\n"
            + "         <mes:Credentials>\n"
            + "            <mes:SystemCode>002</mes:SystemCode>\n"
            + "            <mes:Username>AGENCY</mes:Username>\n"
            + "            <mes:Password>password</mes:Password>\n"
            + "            <mes:Realm></mes:Realm>\n"
            + "            <mes:BankID>01</mes:BankID>\n"
            + "         </mes:Credentials>\n"
            + "      </mes:RequestHeader>\n"
            + "   </soapenv:Header>\n"
            + "   <soapenv:Body>\n"
            + "      <ns:FundsTransferRequest>\n"
            + "         <ns:FundsTransferReqData>\n"
            + "            <ns:OperationParameters>\n"
            + "               <ns:MessageType>NORMAL</ns:MessageType>\n"
            + "               <ns:OperationType>Account</ns:OperationType>\n"
            + "               <ns:UserID></ns:UserID>\n"
            + "               <!--Optional:-->\n"
            + "               <ns:MakerDateTime>" + requestDate + "</ns:MakerDateTime>\n"
            + "               <!--Optional:-->\n"
            + "               <ns:ApproverID></ns:ApproverID>\n"
            + "               <ns:ValueDate>" + valueDate + "</ns:ValueDate>\n"
            + "               <!--Optional:-->\n"
            + "               <ns:ApproverDateTime>" + requestDate + "</ns:ApproverDateTime>\n"
            + "               <!--Optional:-->\n"
            + "               <ns:TransactionID></ns:TransactionID>\n"
            + "               <ns:TransactionType>T</ns:TransactionType>\n"
            + "               <ns:TransactionSubType>BI</ns:TransactionSubType>\n"
            + "               <ns:ExchangeRateDetails>\n"
            + "                  <ns:FromCurrency>KES</ns:FromCurrency>\n"
            + "                  <ns:ExchangeRate>1</ns:ExchangeRate>\n"
            + "                  <ns:ExchangeRateFlag>M</ns:ExchangeRateFlag>\n"
            + "                  <ns:ToCurrency>KES</ns:ToCurrency>\n"
            + "               </ns:ExchangeRateDetails>\n"
            + "            </ns:OperationParameters>\n"
            + "            <ns:TransactionItems>\n"
            + transactionItems
            + "            </ns:TransactionItems>\n"
            + "            <!--Optional:-->\n"
            + "            <ns:TransactionCharges>\n"
            + "               <ns:Charge>\n"
            + "		        <ns:EventType></ns:EventType>\n"
            + "		        <ns:EventId></ns:EventId>\n"
            + "		        <ns:ChargeAccountSerial></ns:ChargeAccountSerial>\n"
            + "		    </ns:Charge>\n"
            + "            </ns:TransactionCharges>\n"
            + "         </ns:FundsTransferReqData>\n"
            + "      </ns:FundsTransferRequest>\n"
            + "   </soapenv:Body>\n"
            + "</soapenv:Envelope>";

            RestTemplate restTemplate = new RestTemplate();

            String url = soaUrl;

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(soaUsername, soaPassword);
            headers.add("SOAPAction", "\"Post\"");
            headers.add("Content-Type", "\"text/xml;charset=UTF-8\"");
            headers.setContentType(MediaType.TEXT_XML);

            HttpEntity<String> entity = new HttpEntity<String>(xml, headers);
            try {
                response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            } catch (Exception ex) {
                response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                ftResp.setStatusDescription("Fail: " + ex.toString());
            }
            if (response.getStatusCode() == HttpStatus.OK) {
                ftResp = extractDetailsFromSOAServiceResponse(response.getBody());
            }
        } catch (Exception ex) {
            response = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ftResp;
    }

    public String createTransactionItems(List<TransactionItems> ti) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < ti.size(); i++) {
            sb.append("<ns:TransactionItem>\n"
            + "<ns:TransactionItemKey>" + ti.get(i).getTransactionItemKey() + "-" + i + "</ns:TransactionItemKey>\n"
            + "<ns:AccountNumber>" + ti.get(i).getAccountNumber() + "</ns:AccountNumber>\n"
            + "<ns:DebitCreditFlag>" + ti.get(i).getDebitCreditFlag() + "</ns:DebitCreditFlag>\n"
            + "<ns:TransactionAmount>" + ti.get(i).getTransactionAmount() + "</ns:TransactionAmount>\n"
            + "<ns:TransactionCurrency>" + ti.get(i).getTransactionCurrency() + "</ns:TransactionCurrency>\n"
            + "<ns:TransactionReference>" + ti.get(i).getTransactionReference() + "</ns:TransactionReference>\n"
            + "<ns:Narrative>" + ti.get(i).getNarrative() + "</ns:Narrative>\n"
            + "<ns:BaseEquivalent>" + ti.get(i).getBaseEquivalent() + "</ns:BaseEquivalent>\n"
            + "<ns:SourceBranch>" + ti.get(i).getSourceBranch() + "</ns:SourceBranch>\n"
            + "<ns:TransactionCode>" + ti.get(i).getTransactionCode() + "</ns:TransactionCode>\n"
            + "<ns:TemporaryODRequired></ns:TemporaryODRequired>\n"
            + "<ns:ChequeOrDraftNumber></ns:ChequeOrDraftNumber>\n"
            + "</ns:TransactionItem>\n");
        }

        return sb.toString();

    }

    public FundsTransferResponseDTO extractDetailsFromSOAServiceResponse(String responseBody) {
        FundsTransferResponseDTO ftResp = new FundsTransferResponseDTO();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(responseBody));
            org.w3c.dom.Document doc = db.parse(is);

            String _status = "";
            String _msgDesc = "";
            String _transactionId = "";
            String _referenceNo = "";

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
                        ftResp.setStatusCode("E_000");
                        ftResp.setStatusDescription("Fail");
                        ftResp.setMessageDescription(_msgDesc);
                    } catch (Exception ex) {
                        ftResp.setStatusCode("E_000");
                        ftResp.setStatusDescription("Fail");
                        ftResp.setMessageDescription("Error Occured ");
                    }
                } else if (_status.equals("S_001")) {
                    NodeList bodyNodes = envElement.getElementsByTagName("soapenv:Body");
                    if (bodyNodes.getLength() > 0) {
                        Element bodyElement = (Element) bodyNodes.item(0);
                        NodeList dataOutputNodes = bodyElement.getElementsByTagName("tns23:FundsTransferResponse");
                        if (dataOutputNodes.getLength() > 0) {
                            Element acctDetailsElement = (Element) dataOutputNodes.item(0);
                            NodeList accountNumberNodes = acctDetailsElement.getElementsByTagName("tns23:MessageReference");
                            int accnumlen = accountNumberNodes.getLength();
                            while/*if*/ (/*accountNumberNodes.getLength()*/accnumlen > 0) {
                                recCnt++;
                                accnumlen--;
                                _referenceNo = accountNumberNodes.item(accnumlen).getTextContent().trim();
                                //  System.out.println("_accountNumber=="+_accountNumber);
                            }

                            NodeList accountNameNodes = acctDetailsElement.getElementsByTagName("tns23:TransactionID");
                            int accnamelen = accountNameNodes.getLength();
                            while/*if*/ (/*accountNumberNodes.getLength()*/accnamelen > 0) {
                                accnamelen--;
                                _transactionId = accountNameNodes.item(accnamelen).getTextContent().trim();
                                //  System.out.println("_accountNumber=="+_accountNumber);
                            }

                            ftResp.setMessageReference(_referenceNo);
                            ftResp.setTransactionID(_transactionId);
                            
                            if (recCnt > 0) {
                                ftResp.setStatusCode("S_001");
                                ftResp.setStatusDescription("Success");
                                ftResp.setMessageDescription(_msgDesc);
                            }

                        }
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ftResp.setStatusCode("E_000");
            ftResp.setStatusDescription("Fail");
            ftResp.setMessageDescription("Error Occured " + e.toString());
        }
        return ftResp;
    }

}
