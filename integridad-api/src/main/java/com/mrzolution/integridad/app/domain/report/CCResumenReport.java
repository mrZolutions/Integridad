package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Data
public class CCResumenReport {
    private String identification;
    private String nameClient;
    private String billNumber;
    private Double billTotal;
    private String tipTransac;
    private String formPago;
    private String numCheque;
    private String fechPago;
    private Double valorAbono;
    private Double valorReten;
    private Double valorNotac;
    
    public CCResumenReport(String identification, String nameClient, String billNumber, Double billTotal, String tipTransac, String formPago, String numCheque, String fechPago, Double valorAbono, Double valorReten, Double valorNotac) {
        this.identification = identification;
        this.nameClient = nameClient;
        this.billNumber = billNumber;
        this.billTotal = billTotal;
        this.tipTransac = tipTransac;
        this.formPago = formPago;
        this.numCheque = numCheque;
        this.fechPago = fechPago;
        this.valorAbono = valorAbono;
        this.valorReten = valorReten;
        this.valorNotac = valorNotac;
    };
}
