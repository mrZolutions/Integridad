package com.mrzolution.integridad.app.domain.report;

import lombok.Data;
import java.util.UUID;

@Data
public class ExistReport {
    private UUID id;
    private String code;
    private String description;
    private Double quantity;
    private Double valUnit;
    
    public ExistReport (UUID id, String code, String description, Double quantity, Double valUnit) {
        this.code = code;
        this.description = description;
        this.quantity = quantity;
        this.valUnit = valUnit;
    }
}
