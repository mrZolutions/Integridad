package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */
@Data
public class CCResumenReport {
    private String idClient;
    private String nameClient;
    private String billNumber;
    private Double billTotal;
    private String tipTransac;
    private String formPago;
    private String fechPago;
    private Double valorAbono;
    private Double valorNotac;
    private Double valorReten;
    
    public CCResumenReport(String idClient, String nameClient, String billNumber, Double billTotal, String tipTransac, String formPago, String fechPago, Double valorAbono, Double valorReten, Double valorNotac) {
        this.idClient = idClient;
        this.nameClient = nameClient;
        this.billNumber = billNumber;
        this.billTotal = billTotal;
        this.tipTransac = tipTransac;
        this.formPago = formPago;
        this.fechPago = fechPago;
        this.valorAbono = valorAbono;
        this.valorReten = valorReten;
        this.valorNotac = valorNotac;
    };
}
