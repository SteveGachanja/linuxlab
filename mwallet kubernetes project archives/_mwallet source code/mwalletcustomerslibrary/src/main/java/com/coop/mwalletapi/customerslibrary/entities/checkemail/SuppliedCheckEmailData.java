package com.coop.mwalletapi.customerslibrary.entities.checkemail;

import com.coop.mwalletapi.commons.RequestHeader;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class SuppliedCheckEmailData extends RequestHeader {
    RequestBody requestBody;
}
