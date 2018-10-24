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
    private double saldo;
    private String statusCredits;
    
    public CreditsReport(String clientName, String ruc, String billNumber, double costo, double saldo, String statusCredits) {
        this.clientName = clientName;
        this.ruc = ruc;
        this.billNumber = billNumber;
        this.costo = costo;
        this.saldo = saldo;
        this.statusCredits = statusCredits;
    };
}
