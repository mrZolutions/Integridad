package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */
@Data
public class CreditsReport {
    private String clientName;
    private String ruc;
    private String billNumber;
    private double costo;
    private double valorAbono;
    private double valorNotac;
    private double valorReten;
    private double valorSubTotal;
    private double saldo;
    private String statusCredits;
    
    public CreditsReport(String clientName, String ruc, String billNumber, double costo, double valorAbono, double valorReten, double valorNotac, double valorSubTotal, double saldo, String statusCredits) {
        this.clientName = clientName;
        this.ruc = ruc;
        this.billNumber = billNumber;
        this.costo = costo;
        this.valorAbono = valorAbono;
        this.valorReten = valorReten;
        this.valorNotac = valorNotac;
        this.valorSubTotal = valorSubTotal;
        this.saldo = saldo;
        this.statusCredits = statusCredits;
    };
}
