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
    private int payNumber;
    private double valor;
    private String statusCredits;
    
    public CreditsReport(String clientName, String ruc, String billNumber, int payNumber, double valor,  String statusCredits) {
        this.clientName = clientName;
        this.ruc = ruc;
        this.billNumber = billNumber;
        this.payNumber = payNumber;
        this.valor = valor;
        this.statusCredits = statusCredits;
    }
}
