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
    private Double costCash;
    private long quantity;
    private Double averageCost;
    private long minorQuantity;
    
    public ExistencyCatReport(String code, String name, Double costCash, long quantity, Double averageCost, long minorQuantity) {
        this.code = code;
        this.name = name;
        this.costCash = costCash;
        this.quantity = quantity;
        this.averageCost = averageCost;
        this.minorQuantity = minorQuantity;
    }
}