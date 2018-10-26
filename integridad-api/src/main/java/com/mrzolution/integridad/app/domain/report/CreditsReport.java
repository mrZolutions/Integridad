package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */
@Data
public class CreditsReport {
    private String clientName;
    private String billNumber;
    private String fechVenta;
    private String fechVence;
    private double costo;
    private double valorAbono;
    private double valorNotac;
    private double valorReten;
    private double saldo;
    
    public CreditsReport(String clientName, String billNumber, String fechVenta, String fechVence, double costo, double valorAbono, double valorReten, double valorNotac, double saldo) {
        this.clientName = clientName;
        this.billNumber = billNumber;
        this.fechVenta = fechVenta;
        this.fechVence = fechVence;
        this.costo = costo;
        this.valorAbono = valorAbono;
        this.valorReten = valorReten;
        this.valorNotac = valorNotac;
        this.saldo = saldo;
    };
}
