package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class CellarEntryReport {
    private String fechaIngreso;
    private String razonSocial;
    private String cellarSeq;
    private String billNumber;
    private String prodName;
    private long prodQuantity;
    private double prodCostEach;
    private double prodIva;
    private double prodTotal;
    
    public CellarEntryReport(String fechaIngreso, String razonSocial, String cellarSeq, String billNumber, String prodName, long prodQuantity, Double prodCostEach, Double prodIva, Double prodTotal) {
        this.fechaIngreso = fechaIngreso;
        this.razonSocial = razonSocial;
        this.cellarSeq = cellarSeq;
        this.billNumber = billNumber;
        this.prodName = prodName;
        this.prodQuantity = prodQuantity;
        this.prodCostEach = prodCostEach;
        this.prodIva = prodIva;
        this.prodTotal = prodTotal;
    }
}