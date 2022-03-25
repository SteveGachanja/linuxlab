package com.coop.mwalletapi.adminlibrary.dao.model;

import com.coop.mwalletapi.commons.ResponseHeader;
import java.util.List;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetSegmentsDataDbResp extends ResponseHeader {
    List<GetSegmentsDataDb> segments;
}
