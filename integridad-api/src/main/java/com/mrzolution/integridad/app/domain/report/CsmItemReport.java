package com.mrzolution.integridad.app.domain.report;

import java.util.UUID;
import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class CsmItemReport {
    private UUID id;
    private String type;
    private String csmNumberSeq;
    private String code;
    private String description;
    private Double quantity;
    private Double valUnit;
    private Double subTotal;
    private Double iva;
    private Double total;

    public CsmItemReport(UUID id, String type, String csmNumberSeq, String code, String description, Double quantity, Double valUnit, Double subTotal, Double iva, Double total) {
        this.type = type;
        this.csmNumberSeq = csmNumberSeq;
        this.code = code;
        this.description = description;
        this.quantity = quantity;
        this.valUnit = valUnit;
        this.subTotal = subTotal;
        this.iva = iva;
        this.total = total;
    }
}