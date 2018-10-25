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
    private double valorSubTotal;
    private double valorTotal;
    
    public CCResumenReport(String idClient, String nameClient, String billNumber, double billTotal, String tipTransac, String formPago, String fechPago, double valorAbono, double valorReten, double valorSubTotal,double valorTotal){
        this.idClient = idClient;
        this.nameClient = nameClient;
        this.billNumber = billNumber;
        this.billTotal = billTotal;
        this.tipTransac = tipTransac;
        this.formPago = formPago;
        this.fechPago = fechPago;
        this.valorAbono = valorAbono;
        this.valorReten = valorReten;
        this.valorSubTotal = valorSubTotal;
        this.valorTotal = valorTotal;
    };
}
