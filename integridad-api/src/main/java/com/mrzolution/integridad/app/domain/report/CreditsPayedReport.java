package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class CreditsPayedReport {
    private String clientName;
    private String ruc;
    private String billNumber;
    private Double costo;
    private Double saldo;
    private String statusCredits;
    
    public CreditsPayedReport(String clientName, String ruc, String billNumber, Double costo, Double saldo, String statusCredits) {
        this.clientName = clientName;
        this.ruc = ruc;
        this.billNumber = billNumber;
        this.costo = costo;
        this.saldo = saldo;
        this.statusCredits = statusCredits;
    };
}