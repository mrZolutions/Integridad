package com.mrzolution.integridad.app.domain.report;

import java.util.UUID;
import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class ItemOfflineReport {
    private UUID id;
    private String billOffSeqString;
    private String code;
    private String description;
    private Double quantity;
    private Double valUnit;
    private Double subTotal;
    private Double discount;
    private Double iva;
    private Double total;

    public ItemOfflineReport(UUID id, String billOffSeqString, String code, String description, Double quantity, Double valUnit, Double subTotal, Double discount, Double iva, Double total) {
        this.billOffSeqString = billOffSeqString;
        this.code = code;
        this.description = description;
        this.quantity = quantity;
        this.valUnit = valUnit;
        this.subTotal = subTotal;
        this.discount = discount;
        this.iva = iva;
        this.total = total;
    }
}