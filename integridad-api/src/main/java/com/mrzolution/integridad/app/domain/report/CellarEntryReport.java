package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class CellarEntryReport {
    private String fechaIngreso;
    private String whNumberSeq;
    private String razonSocial;
    private String fechaBill;
    private String billNumber;
    private String prodName;
    private long prodQuantity;
    private double prodCostEach;
    private double prodIva;
    private double prodTotal;
    
    public CellarEntryReport(String fechaIngreso, String whNumberSeq, String razonSocial, String fechaBill, String billNumber, String prodName, long prodQuantity, Double prodCostEach, Double prodIva, Double prodTotal) {
        this.fechaIngreso = fechaIngreso;
        this.whNumberSeq = whNumberSeq;
        this.razonSocial = razonSocial;
        this.fechaBill = fechaBill;
        this.billNumber = billNumber;
        this.prodName = prodName;
        this.prodQuantity = prodQuantity;
        this.prodCostEach = prodCostEach;
        this.prodIva = prodIva;
        this.prodTotal = prodTotal;
    }
}