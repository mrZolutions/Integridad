package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

@Data
public class ExistReport {
    private String code;
    private String name;
    private Long quantity;
    private Double averageCost;
    private Long max_minimun;
    
    public ExistReport (String code, String name, Double averageCost, Long max_minimun) {
        this.code = code;
        this.name = name;
        this.averageCost = averageCost;
        this.max_minimun = max_minimun;
    }
}
