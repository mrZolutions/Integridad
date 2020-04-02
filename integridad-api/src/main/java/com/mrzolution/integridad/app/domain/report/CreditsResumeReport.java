package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel
 */

@Data
public class CreditsResumeReport {
    private String identification;
    private String clientName;
    private String billNumber;
    private String fechVenta;
    private int diasCredit;
    private int diasVencim;
    private double costo;
    private double valorAbono;
    private double valorNotac;
    private double valorReten;
    private double saldo;

    public CreditsResumeReport(){
    }

    public CreditsResumeReport(String identification, String clientName, String billNumber, String fechVenta, double costo, double saldo) {
        this.identification = identification;
        this.clientName = clientName;
        this.billNumber = billNumber;
        this.fechVenta = fechVenta;
        this.costo = costo;
        this.saldo = saldo;
    }
}