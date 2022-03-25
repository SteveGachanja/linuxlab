package com.coop.mwalletapi.accountstatementlibrary.entities;

import java.util.Date;
import lombok.Data;

/**
 * @author cndirangu
 */

@Data
public class RequestBody {
   String accountAlias;
   int maxRows;
   String transactionId;
   Date startDate;
   Date endDate;
}
