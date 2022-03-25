package com.coop.mwalletapi.adminlibrary.entities.getsegments.response;

import com.coop.mwalletapi.adminlibrary.dao.model.GetSegmentsDataDb;
import java.util.List;
import lombok.Data;

/**
 * @author okahia
 */

@Data
public class GetSegmentsResponseData {
    List<GetSegmentsDataDb> segments;
}
