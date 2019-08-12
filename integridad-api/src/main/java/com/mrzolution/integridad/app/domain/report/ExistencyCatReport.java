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
    private Double costCash;
    private Double costCard;
    private Double costCredit;
    private Double costMajor;
    private long quantity;
    
    public ExistencyCatReport(String code, String name, Double costReal, Double costCash, Double costCard, Double costCredit, Double costMajor, long quantity) {
        this.code = code;
        this.name = name;
        this.costReal = costReal;
        this.costCash = costCash;
        this.costCard = costCard;
        this.costCredit = costCredit;
        this.costMajor = costMajor;
        this.quantity = quantity;
    }
}