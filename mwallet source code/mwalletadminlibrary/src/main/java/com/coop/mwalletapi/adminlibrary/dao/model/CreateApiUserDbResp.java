package com.coop.mwalletapi.adminlibrary.dao.model;
import com.coop.mwalletapi.commons.ResponseHeader;
import lombok.Data;
/**
 * @author okahia
 */

@Data
public class CreateApiUserDbResp extends ResponseHeader {
    String apiUserId;
    String apiUserName;
}
