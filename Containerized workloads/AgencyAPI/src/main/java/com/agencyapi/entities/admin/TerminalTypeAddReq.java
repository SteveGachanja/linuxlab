package com.agencyapi.entities.admin;

import com.agencyapi.common.RequestHeader;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author okahia
 */

@Data
public class TerminalTypeAddReq  extends RequestHeader{
    ArrayList < RequestBodyAdd > requestBody = new ArrayList <> ();
}
