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
    private double billTotal;
    private String tipTransac;
    private String formPago;
    private String fechPago;
    private double valorAbono;
    private double valorNotac;
    private double valorReten;
    
    public CCResumenReport(String idClient, String nameClient, String billNumber, double billTotal, String tipTransac, String formPago, String fechPago, double valorAbono, double valorReten, double valorNotac){
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
