package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Data
public class ExistencyReport {
    private String code;
    private String name;
    private Double costReal;
    private Double costCard;
    private Double costCash;
    private Double costCredit;
    private Double costMajor;
    private long maxMin;
    private long quantity;
    private String group;
    private String subGroup;
    private String marca;
    private String linea;
    
    public ExistencyReport(String code, String name, Double costReal, Double costCard, Double costCash, Double costCredit, Double costMajor, long maxMin, long quantity, String group, String subGroup, String marca, String linea) {
        this.code = code;
        this.name = name;
        this.costReal = costReal;
        this.costCard = costCard;
        this.costCash = costCash;
        this.costCredit = costCredit;
        this.costMajor = costMajor;
        this.maxMin = maxMin;
        this.quantity = quantity;
        this.group = group;
        this.subGroup = subGroup;
        this.marca = marca;
        this.linea = linea;
    }
}