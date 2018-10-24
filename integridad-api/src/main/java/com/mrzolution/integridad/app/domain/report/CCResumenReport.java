package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */
@Data
public class CCResumenReport {
    private String codCliente;
    private String nomCliente;
    private String tipTransac;
    private String formPago;
    private String fechPago;
    private String billNumber;
    private double valorTotal;
    
    public CCResumenReport(String codCliente, String nomCliente, String tipTransac, String formPago, String fechPago, String billNumber, double valorTotal){
        this.codCliente = codCliente;
        this.nomCliente = nomCliente;
        this.tipTransac = tipTransac;
        this.formPago = formPago;
        this.fechPago = fechPago;
        this.billNumber = billNumber;
        this.valorTotal = valorTotal;
    };
}
