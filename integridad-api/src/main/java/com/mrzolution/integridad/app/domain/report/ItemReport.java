package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

import java.util.UUID;

@Data
public class ItemReport {

    private UUID id;
    private String type;
    private String billSeqString;
    private String code;
    private String description;
    private Double quantity;
    private Double valUnit;
    private Double subTotal;
    private Double iva;
    private Double total;

    public ItemReport(UUID id, String type, String billSeqString, String code, String description, Double quantity, Double valUnit, Double subTotal, Double iva, Double total) {
        this.type = type;
        this.billSeqString = billSeqString;
        this.code = code;
        this.description = description;
        this.quantity = quantity;
        this.valUnit = valUnit;
        this.subTotal = subTotal;
        this.iva = iva;
        this.total = total;
    }
}
