package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class ExistencyCatReport {
    private String code;
    private String name;
    private Double costReal;
    private long quantity;
    private Double averCost;
    private long minorQuantity;
    
    public ExistencyCatReport(String code, String name, Double costReal, long quantity, Double averCost, long minorQuantity) {
        this.code = code;
        this.name = name;
        this.costReal = costReal;
        this.quantity = quantity;
        this.averCost = averCost;
        this.minorQuantity = minorQuantity;
    }
}