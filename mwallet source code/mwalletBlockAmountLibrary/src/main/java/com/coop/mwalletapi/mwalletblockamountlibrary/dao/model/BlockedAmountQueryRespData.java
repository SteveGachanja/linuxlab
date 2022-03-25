package com.coop.mwalletapi.mwalletblockamountlibrary.dao.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fosano
 */

@Getter
@Setter
public class BlockedAmountQueryRespData {
    public int statusCode;
    public String statusDescription;
    public List<BlockedAmountDetails> blockedAmountListing = new ArrayList<>();
}
