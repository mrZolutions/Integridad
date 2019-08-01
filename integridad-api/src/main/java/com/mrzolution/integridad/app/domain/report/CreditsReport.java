package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author mrzolutions-daniel
 */

@Data
public class CreditsReport {
    private String identification;
    private String clientName;
    private String billNumber;
    private String fechVenta;
    private String fechVence;
    private int diasCredit;
    private int diasVencim;
    private double costo;
    private double valorAbono;
    private double valorNotac;
    private double valorReten;
    private double saldo;
    private double pplazo;
    private double splazo;
    private double tplazo;
    private double cplazo;
    private double qplazo;
    
    public CreditsReport(String identification, String clientName, String billNumber, String fechVenta, String fechVence, int diasCredit, int diasVencim, double costo, double valorAbono, double valorReten, double valorNotac, double saldo, double pplazo, double splazo, double tplazo, double cplazo, double qplazo) {
        this.identification = identification;
        this.clientName = clientName;
        this.billNumber = billNumber;
        this.fechVenta = fechVenta;
        this.fechVence = fechVence;
        this.diasCredit = diasCredit;
        this.diasVencim = diasVencim;
        this.costo = costo;
        this.valorAbono = valorAbono;
        this.valorReten = valorReten;
        this.valorNotac = valorNotac;
        this.saldo = saldo;
        this.pplazo = pplazo;
        this.splazo = splazo;
        this.tplazo = tplazo;
        this.cplazo = cplazo;
        this.qplazo = qplazo;
    }
}