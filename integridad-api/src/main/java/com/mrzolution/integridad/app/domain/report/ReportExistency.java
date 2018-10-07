/**
 *
 * @author daniel-one
 */
package com.mrzolution.integridad.app.domain.report;

import java.util.UUID;
import lombok.Data;

@Data
public class ReportExistency {
    private UUID id;
    private String code;
    private String name;
    private Double cost;
    private int maxMin;
    private int quantity;
    
    public ReportExistency(String code, String name, Double cost, int maxMin, int quantity) {
        this.code = code;
        this.name = name;
        this.cost = cost;
        this.maxMin = maxMin;
        this.quantity = quantity;
    }
}
