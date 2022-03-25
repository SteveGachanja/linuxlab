package com.coop.mwalletapi.transactions.entities;

import lombok.Data;

/**
 * @author okahia
 */

@Data
public class ExchangeRateDetails {
    private String fromCurrency;
    private String exchangeRate;
    private String exchangeRateFlag;
    private String toCurrency;
}
