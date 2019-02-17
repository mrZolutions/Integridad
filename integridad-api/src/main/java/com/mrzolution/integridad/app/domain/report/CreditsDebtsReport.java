package com.mrzolution.integridad.app.domain.report;

import lombok.Data;

/**
 *
 * @author daniel-one
 */

@Data
public class CreditsDebtsReport {
    private String providerName;
    private String billNumber;
    private String fechVenta;
    private String fechVence;
    private int diasCredit;
    private int diasVencim;
    private Double total;
    private Double valorAbono;
    private Double valorReten;
    private Double saldo;
    
    public CreditsDebtsReport(String providerName, String billNumber, String fechVenta, String fechVence, int diasCredit, int diasVencim, Double total, Double valorAbono, Double valorReten, Double saldo) {
        this.providerName = providerName;
        this.billNumber =  billNumber;
        this.fechVenta =  fechVenta;
        this.fechVence =  fechVence;
        this.diasCredit =  diasCredit;
        this.diasVencim =  diasVencim;
        this.total =  total;
        this.valorAbono =  valorAbono;
        this.valorReten =  valorReten;
        this.saldo =  saldo;
    }
}
