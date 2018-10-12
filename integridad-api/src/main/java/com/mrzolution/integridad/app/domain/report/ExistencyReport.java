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
    private Double cost;
    private int maxMin;
    private int quantity;
    
    public ExistencyReport(String code, String name, Double cost, int maxMin, int quantity) {
        this.code = code;
        this.name = name;
        this.cost = cost;
        this.maxMin = maxMin;
        this.quantity = quantity;
    }
}
