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
    private long maxMin;
    private long quantity;
    
    public ExistencyReport(String code, String name, Double cost, long maxMin, long quantity) {
        this.code = code;
        this.name = name;
        this.cost = cost;
        this.maxMin = maxMin;
        this.quantity = quantity;
    }
}
