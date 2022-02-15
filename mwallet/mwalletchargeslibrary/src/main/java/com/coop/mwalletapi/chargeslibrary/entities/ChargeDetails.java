/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.coop.mwalletapi.chargeslibrary.entities;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author cndirangu
 */
@Getter
@Setter
public class ChargeDetails {
    String  chargeCode;
    double chargeAmount;
    String chargeGlAccount;
    String chargeType;
    String applyChargeTo;
}
