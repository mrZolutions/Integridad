package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class DetailListReport {
    private String billNumber;
    private long quantity;
    private Double costEach;
    private Double total;
    private String adicional; 
    
    public DetailListReport(String billNumber, long quantity, Double costEach, Double total, String adicional) {
        
    }
}
