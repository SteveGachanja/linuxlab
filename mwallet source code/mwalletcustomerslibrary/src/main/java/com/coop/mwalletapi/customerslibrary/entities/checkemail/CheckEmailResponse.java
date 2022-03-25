package com.coop.mwalletapi.customerslibrary.entities.checkemail;

import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class CheckEmailResponse extends ResponseHeader {
    CheckEmailResponseData responseBody;
}
